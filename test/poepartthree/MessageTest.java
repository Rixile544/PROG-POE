
package poepartthree;

import org.junit.Test;
import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void testGenerateMessageID() {
        Message message = new Message();
        String messageID = message.generateMessageID();

        assertNotNull(messageID);
        assertEquals(10, messageID.length());
        assertTrue(messageID.matches("[0-9]{10}"));
    }

    @Test
    public void testCreateMessageHash() {
        Message message = new Message();

        String hash = message.createMessageHash(
                "1234567890", 1, "Hello world");

        assertEquals("12:1:HELLOWORLD", hash);
    }

    @Test
    public void testCheckRecipientCellValidLocalNumber() {
        Message message = new Message();

        assertEquals("Valid number",
                message.checkRecipientCell("0123456789"));
    }

    @Test
    public void testCheckRecipientCellValidInternationalNumber() {
        Message message = new Message();

        assertEquals("Valid number",
                message.checkRecipientCell("+27123456789"));
    }

    @Test
    public void testCheckRecipientCellInvalidNumber() {
        Message message = new Message();

        assertEquals("Invalid number",
                message.checkRecipientCell("12345"));
    }

    @Test
public void testReturnTotalMessages() {
    Message message = new Message();

    assertEquals(0, message.returnTotalMessages());

    message.addSentMessage("Hello world");

    assertEquals(1, message.returnTotalMessages());

    message.addSentMessage("Good morning");

    assertEquals(2, message.returnTotalMessages());
}
    @Test
public void testGenerateUniqueMessageIDs() {
    Message message = new Message();

    String firstID = message.generateMessageID();
    String secondID = message.generateMessageID();

    assertNotEquals(firstID, secondID);
}
@Test
public void testDifferentMessagesProduceDifferentHashes() {
    Message message = new Message();

    String firstHash = message.createMessageHash(
            "1234567890", 1, "Hello world");

    String secondHash = message.createMessageHash(
            "1234567891", 2, "Good morning");

    assertNotEquals(firstHash, secondHash);
}
@Test
public void testCheckRecipientCellNull() {
    Message message = new Message();

    assertEquals("Invalid number",
            message.checkRecipientCell(null));
}
@Test
public void testPrintMessagesEmpty() {
    Message message = new Message();

    assertTrue(message.printMessages().contains("All Messages"));
    assertFalse(message.printMessages().contains("Message ID:"));
}
}
