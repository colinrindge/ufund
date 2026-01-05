package com.ufund.api.ufundapi.persistence;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.ChatPersonality;

@Tag("Persitence-Tier")
class ChatMemoryDAOTest {
    private ChatMemoryDAO chatMemoryDAO;
    private ObjectMapper objectMapper;
    private ChatPersonality chatPersonality;

    @BeforeEach
    void setupChatMemoryDAO() {
        objectMapper = new ObjectMapper();
        chatMemoryDAO = new ChatMemoryDAO(objectMapper);
        chatPersonality = new ChatPersonality(0, "Bob", "Hello. Please do not respond to this with more than one word.");
    }

    @AfterEach
    void removeChat() {
        if (chatMemoryDAO.chatExists(1)) {
            chatMemoryDAO.deleteChat(1);
        }
    }

    @Test
    void testCreateChat() {
        int id = 1;
        int testchat = chatMemoryDAO.generateChat(id, chatPersonality);
        assertEquals(testchat, id);
    }

    @Test 
    void testCreateChatAlreadyExists() {
        int id = 1;
        chatMemoryDAO.generateChat(id, chatPersonality);
        Integer testchat2 = chatMemoryDAO.generateChat(id, chatPersonality);
        assertNull(testchat2);
    }

    @Test
    void testDeleteChat() {
        chatMemoryDAO.generateChat(1, chatPersonality);
        Boolean result = chatMemoryDAO.deleteChat(1);
        assertTrue(result);
    }

    @Test
    void testDeleteChatDoesNotExist() {
        Boolean result = chatMemoryDAO.deleteChat(1);
        assertFalse(result);
    }

    @Test
    void testSendChat() {
        int id = 1;
        String message = "Hello! This is a test of your functionality";
        chatMemoryDAO.generateChat(id, chatPersonality);
        String result = chatMemoryDAO.submitChat(id, message);
        assertTrue(!result.isEmpty());
    }

    @Test
    void testSendChatSerializationFailure() {
        String result = assertDoesNotThrow(()-> chatMemoryDAO.submitChat(1, ""),
        "Unexpected exception thrown");
        assertNull(result);
    }

    @Test
    void testSendChatChatDoesNotExist() {
        int id = 1;
        String message = "Hello! This is a test of your functionality";
        String result = chatMemoryDAO.submitChat(id, message);
        assertNull(result);
    }

    @Test
    void testChatExists() {
        chatMemoryDAO.generateChat(1, chatPersonality);
        assertTrue(chatMemoryDAO.chatExists(1));
    }

    @Test
    void testChatExistsWhenChatDNE() {
        assertFalse(chatMemoryDAO.chatExists(1));
    }

    @Test
    void testGetPersonalities() {
        ChatPersonality[] personalities = chatMemoryDAO.getPersonalities();
        assertNotNull(personalities);
    }
}
