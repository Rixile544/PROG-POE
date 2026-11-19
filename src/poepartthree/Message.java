

package poepartthree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * Handles messages, message storage, searching and reporting.
 *
 * @author Student
 */
public class Message {

    ArrayList<String> sentMessages = new ArrayList<>();
    ArrayList<String> messageIdsList = new ArrayList<>();
    HashSet<String> messageIds = new HashSet<>();
    ArrayList<String> msgHash = new ArrayList<>();
    ArrayList<String> recipientNumber = new ArrayList<>();
    ArrayList<String> messageStatus = new ArrayList<>();
    ArrayList<String> disregardedMessages = new ArrayList<>();
    ArrayList<String> storedMessages = new ArrayList<>();
    ArrayList<String> senders = new ArrayList<>();

    Random random = new Random();

    int numMessageSent = 0;

    private final Path jsonFile = Path.of("storedMessages.json");

    public Message() {
        loadStoredMessagesFromJson();
    }

    public void sendingMessages() {

        String input = JOptionPane.showInputDialog(null,
                "How many messages do you want to send?",
                "Send Messages",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            return;
        }

        int numberOfMessages;

        try {
            numberOfMessages = Integer.parseInt(input.trim());

            if (numberOfMessages < 1) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(null,
                    "Please enter a valid number greater than 0.",
                    "Invalid number",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 1; i <= numberOfMessages; i++) {

            String recipient = JOptionPane.showInputDialog(null,
                    "Message " + i + " of " + numberOfMessages
                    + "\nEnter recipient cell number:",
                    "Recipient",
                    JOptionPane.QUESTION_MESSAGE);

            if (recipient == null) {
                return;
            }

            recipient = recipient.trim();

            if (!checkRecipientCell(recipient).equals("Valid number")) {

                JOptionPane.showMessageDialog(null,
                        "Invalid recipient number. Use 0XXXXXXXXX or +27XXXXXXXXX.",
                        "Invalid number",
                        JOptionPane.ERROR_MESSAGE);

                i--;
                continue;
            }

            String messageText = JOptionPane.showInputDialog(null,
                    "Enter your message (1 to 250 characters):",
                    "Message",
                    JOptionPane.QUESTION_MESSAGE);

            if (messageText == null) {
                return;
            }

            if (messageText.trim().isEmpty()
                    || messageText.length() > 250) {

                JOptionPane.showMessageDialog(null,
                        "Please enter a message of 1 to 250 characters.",
                        "Invalid message",
                        JOptionPane.ERROR_MESSAGE);

                i--;
                continue;
            }

            messageText = messageText.trim();

            String sender = JOptionPane.showInputDialog(null,
                    "Enter sender name:",
                    "Sender",
                    JOptionPane.QUESTION_MESSAGE);

            if (sender == null || sender.trim().isEmpty()) {
                sender = "unknown";
            }

            sender = sender.trim();

            // Generate the Message ID and Hash ID.
            String messageID = generateMessageID();

            int messageNumber = storedMessages.size() + 1;

            String messageHash = createMessageHash(
                    messageID, messageNumber, messageText);

            // Select what to do with the message.
            String status = SentMessage();

            // Store all message details at matching indexes.
            storedMessages.add(messageText);
            messageIdsList.add(messageID);
            messageIds.add(messageID);
            msgHash.add(messageHash);
            recipientNumber.add(recipient);
            senders.add(sender);
            messageStatus.add(status);

            if (status.equals("Sent")) {

                addSentMessage(messageText);

            } else if (status.equals("Stored")) {

                saveStoredMessagesToJson();

            } else {

                disregardedMessages.add(messageText);
            }

            // Display the Message ID and Hash ID after processing.
            JOptionPane.showMessageDialog(null,
                    "Message processing completed.\n\n"
                    + "Message ID: " + messageID
                    + "\nMessage Hash (Hash ID): " + messageHash
                    + "\nRecipient: " + recipient
                    + "\nSender: " + sender
                    + "\nMessage: " + messageText
                    + "\nStatus: " + status
                    + "\nTotal messages sent: " + returnTotalMessages(),
                    "Message Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public int returnTotalMessages() {
    return numMessageSent;
}

public void addSentMessage(String messageText) {
    sentMessages.add(messageText);
    numMessageSent++;
}

    public String createMessageHash(
            String messageID, int messageNumber, String messageText) {

        String cleanMessage = messageText.trim();

        String[] words = cleanMessage.split("\\s+");

        String firstWord = words[0];

        String lastWord = words[words.length - 1];

        return (messageID.substring(0, 2) + ":"
                + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    public String generateMessageID() {

        String messageID;

        do {
            long number = 1000000000L
                    + random.nextLong(9000000000L);

            messageID = String.valueOf(number);

        } while (messageIds.contains(messageID));

        return messageID;
    }

    public String checkRecipientCell(String cellphone) {

        if (cellphone != null
                && (cellphone.matches("^\\+27[0-9]{9}$")
                || cellphone.matches("^0[0-9]{9}$"))) {

            return "Valid number";
        }

        return "Invalid number";
    }

    public String SentMessage() {

        Object[] options = {"Send", "Disregard", "Store"};

        int choice = JOptionPane.showOptionDialog(null,
                "Choose what you want to do with this message:",
                "Message Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            return "Sent";
        }

        if (choice == 1) {

            int confirm = JOptionPane.showConfirmDialog(null,
                    "Do you want to disregard this message?",
                    "Disregard Message",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                return "Disregarded";
            }

            return "Stored";
        }

        return "Stored";
    }

    public String printMessages() {

        StringBuilder output = new StringBuilder();

        output.append("===== All Messages =====\n");

        for (int i = 0; i < storedMessages.size(); i++) {

            output.append("\nMessage Number: ").append(i + 1)
                    .append("\nMessage ID: ").append(messageIdsList.get(i))
                    .append("\nRecipient: ").append(recipientNumber.get(i))
                    .append("\nSender: ").append(senders.get(i))
                    .append("\nMessage: ").append(storedMessages.get(i))
                    .append("\nMessage Hash: ").append(msgHash.get(i))
                    .append("\nStatus: ").append(messageStatus.get(i))
                    .append("\n");
        }

        return output.toString();
    }

    public void messageManagementMenu() {

        while (true) {

            String option = JOptionPane.showInputDialog(null,
                    "1. Show disregarded messages\n"
                    + "2. Show all stored messages\n"
                    + "3. Delete a message by Hash ID\n"
                    + "4. Display full report\n"
                    + "5. Search for a message by Hash ID\n"
                    + "6. Display the longest stored message\n"
                    + "7. Search messages by recipient\n"
                    + "8. Back to main menu",
                    "Stored Messages Menu",
                    JOptionPane.QUESTION_MESSAGE);

            if (option == null || option.equals("8")) {
                return;
            }

            switch (option) {

                case "1":
                    showDisregardedMessages();
                    break;

                case "2":
                    showAllStoredMessages();
                    break;

                case "3":
                    deleteMessageByHash();
                    break;

                case "4":
                    displayFullReport();
                    break;

                case "5":
                    searchMessageByHashId();
                    break;

                case "6":
                    findLongestMessage();
                    break;

                case "7":
                    searchMessagesByRecipient();
                    break;

                default:
                    JOptionPane.showMessageDialog(null,
                            "Please choose an option from 1 to 8.");
            }
        }
    }

    public void findLongestMessage() {

        int index = -1;
        int longestLength = -1;

        for (int i = 0; i < storedMessages.size(); i++) {

            if (messageStatus.get(i).equals("Stored")
                    && storedMessages.get(i).length() > longestLength) {

                longestLength = storedMessages.get(i).length();
                index = i;
            }
        }

        if (index == -1) {

            JOptionPane.showMessageDialog(null,
                    "No stored messages to display.");

            return;
        }

        JOptionPane.showMessageDialog(null,
                "Longest stored message:\n"
                + storedMessages.get(index)
                + "\n\nMessage ID: " + messageIdsList.get(index)
                + "\nMessage Hash: " + msgHash.get(index)
                + "\nRecipient: " + recipientNumber.get(index)
                + "\nLength: " + longestLength + " characters",
                "Longest Message",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void searchMessageByHashId() {

        String hashId = JOptionPane.showInputDialog(null,
                "Enter the Hash ID to search:",
                "Search Message",
                JOptionPane.QUESTION_MESSAGE);

        if (hashId == null) {
            return;
        }

        hashId = hashId.trim();

        for (int i = 0; i < msgHash.size(); i++) {

            if (msgHash.get(i).equalsIgnoreCase(hashId)) {

                showMessageDetails(i);
                return;
            }
        }

        JOptionPane.showMessageDialog(null,
                "No message exists with Hash ID: " + hashId);
    }

    public void searchMessageById() {

        String id = JOptionPane.showInputDialog(null,
                "Enter the Message ID to search:",
                "Search Message",
                JOptionPane.QUESTION_MESSAGE);

        if (id == null) {
            return;
        }

        id = id.trim();

        for (int i = 0; i < messageIdsList.size(); i++) {

            if (messageIdsList.get(i).equals(id)) {

                showMessageDetails(i);
                return;
            }
        }

        JOptionPane.showMessageDialog(null,
                "No message exists with Message ID: " + id);
    }

    public void searchMessagesByRecipient() {

        String recipient = JOptionPane.showInputDialog(null,
                "Enter recipient number:",
                "Search by Recipient",
                JOptionPane.QUESTION_MESSAGE);

        if (recipient == null) {
            return;
        }

        recipient = recipient.trim();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < storedMessages.size(); i++) {

            String status = messageStatus.get(i);

            if (recipientNumber.get(i).equals(recipient)
                    && (status.equals("Sent") || status.equals("Stored"))) {

                result.append("Message: ")
                        .append(storedMessages.get(i))
                        .append("\nHash ID: ")
                        .append(msgHash.get(i))
                        .append("\nMessage ID: ")
                        .append(messageIdsList.get(i))
                        .append("\n\n");
            }
        }

        JOptionPane.showMessageDialog(null,
                result.length() == 0
                ? "No messages found for this recipient."
                : result.toString(),
                "Recipient Search",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void displayFullReport() {

        if (storedMessages.isEmpty()) {

            JOptionPane.showMessageDialog(null,
                    "No messages available for the report.");

            return;
        }

        JOptionPane.showMessageDialog(null,
                printMessages(),
                "Full Message Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMessageDetails(int index) {

        JOptionPane.showMessageDialog(null,
                "Hash ID: " + msgHash.get(index)
                + "\nMessage ID: " + messageIdsList.get(index)
                + "\nFrom: " + senders.get(index)
                + "\nTo: " + recipientNumber.get(index)
                + "\nMessage: " + storedMessages.get(index)
                + "\nStatus: " + messageStatus.get(index)
                + "\nLength: " + storedMessages.get(index).length()
                + " characters",
                "Message Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void deleteMessageByHash() {

        String hash = JOptionPane.showInputDialog(null,
                "Enter the message Hash ID to delete:",
                "Delete Message",
                JOptionPane.QUESTION_MESSAGE);

        if (hash == null) {
            return;
        }

        hash = hash.trim();

        int index = -1;

        for (int i = 0; i < msgHash.size(); i++) {

            if (msgHash.get(i).equalsIgnoreCase(hash)) {
                index = i;
                break;
            }
        }

        if (index == -1) {

            JOptionPane.showMessageDialog(null,
                    "No message exists with Hash ID: " + hash);

            return;
        }

        String message = storedMessages.get(index);
        String status = messageStatus.get(index);

        if (status.equals("Sent")) {

            sentMessages.remove(message);
            numMessageSent = Math.max(0, numMessageSent - 1);

        } else if (status.equals("Disregarded")) {

            disregardedMessages.remove(message);
        }

        messageIds.remove(messageIdsList.get(index));

        messageIdsList.remove(index);
        storedMessages.remove(index);
        msgHash.remove(index);
        recipientNumber.remove(index);
        senders.remove(index);
        messageStatus.remove(index);

        saveStoredMessagesToJson();

        JOptionPane.showMessageDialog(null,
                "Message successfully deleted.");
    }

    public void showAllStoredMessages() {

        StringBuilder result = new StringBuilder(
                "===== ALL STORED MESSAGES =====\n\n");

        for (int i = 0; i < storedMessages.size(); i++) {

            if (messageStatus.get(i).equals("Stored")) {

                result.append("Message ID: ")
                        .append(messageIdsList.get(i))
                        .append("\nSender: ")
                        .append(senders.get(i))
                        .append("\nRecipient: ")
                        .append(recipientNumber.get(i))
                        .append("\nMessage: ")
                        .append(storedMessages.get(i))
                        .append("\nMessage Hash: ")
                        .append(msgHash.get(i))
                        .append("\n\n");
            }
        }

        if (result.toString().equals(
                "===== ALL STORED MESSAGES =====\n\n")) {

            JOptionPane.showMessageDialog(null,
                    "No stored messages found.");

        } else {

            JOptionPane.showMessageDialog(null,
                    result.toString(),
                    "Stored Messages",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showDisregardedMessages() {

        if (disregardedMessages.isEmpty()) {

            JOptionPane.showMessageDialog(null,
                    "No disregarded messages found.");

            return;
        }

        StringBuilder result = new StringBuilder(
                "===== DISREGARDED MESSAGES =====\n\n");

        for (String message : disregardedMessages) {
            result.append(message).append("\n\n");
        }

        JOptionPane.showMessageDialog(null, result.toString());
    }

    public void saveStoredMessagesToJson() {

        StringBuilder json = new StringBuilder("[\n");

        boolean first = true;

        for (int i = 0; i < storedMessages.size(); i++) {

            if (!messageStatus.get(i).equals("Stored")) {
                continue;
            }

            if (!first) {
                json.append(",\n");
            }

            json.append("  {\n")
                    .append("    \"messageID\": \"")
                    .append(escapeJson(messageIdsList.get(i))).append("\",\n")
                    .append("    \"hash\": \"")
                    .append(escapeJson(msgHash.get(i))).append("\",\n")
                    .append("    \"sender\": \"")
                    .append(escapeJson(senders.get(i))).append("\",\n")
                    .append("    \"recipient\": \"")
                    .append(escapeJson(recipientNumber.get(i))).append("\",\n")
                    .append("    \"message\": \"")
                    .append(escapeJson(storedMessages.get(i))).append("\",\n")
                    .append("    \"status\": \"Stored\"\n")
                    .append("  }");

            first = false;
        }

        json.append("\n]\n");

        try {

            Files.writeString(jsonFile, json.toString());

        } catch (IOException e) {

            JOptionPane.showMessageDialog(null,
                    "Could not save stored messages: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadStoredMessagesFromJson() {

        if (!Files.exists(jsonFile)) {
            return;
        }

        try {

            String json = Files.readString(jsonFile);

            Pattern objectPattern = Pattern.compile("\\{([^{}]*)\\}");

            Matcher objectMatcher = objectPattern.matcher(json);

            while (objectMatcher.find()) {

                String object = objectMatcher.group(1);

                String id = readJsonField(object, "messageID");
                String hash = readJsonField(object, "hash");
                String sender = readJsonField(object, "sender");
                String recipient = readJsonField(object, "recipient");
                String message = readJsonField(object, "message");

                if (id == null || hash == null || sender == null
                        || recipient == null || message == null
                        || messageIds.contains(id)) {

                    continue;
                }

                messageIdsList.add(id);
                messageIds.add(id);
                msgHash.add(hash);
                senders.add(sender);
                recipientNumber.add(recipient);
                storedMessages.add(message);
                messageStatus.add("Stored");
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(null,
                    "Could not read stored messages: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String readJsonField(String object, String field) {

        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(field)
                + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""
        );

        Matcher matcher = pattern.matcher(object);

        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }

        return null;
    }

    private String escapeJson(String value) {

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String value) {

        StringBuilder result = new StringBuilder();

        boolean escaped = false;

        for (int i = 0; i < value.length(); i++) {

            char c = value.charAt(i);

            if (escaped) {

                switch (c) {

                    case 'n':
                        result.append('\n');
                        break;

                    case 'r':
                        result.append('\r');
                        break;

                    case 't':
                        result.append('\t');
                        break;

                    default:
                        result.append(c);
                }

                escaped = false;

            } else if (c == '\\') {

                escaped = true;

            } else {

                result.append(c);
            }
        }

        if (escaped) {
            result.append('\\');
        }

        return result.toString();
    }
}
