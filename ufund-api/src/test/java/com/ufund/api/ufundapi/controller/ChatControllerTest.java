package com.ufund.api.ufundapi.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.ufund.api.ufundapi.model.ChatPersonality;
import com.ufund.api.ufundapi.persistence.ChatDAO;

@Tag("Controller-Tier")
class ChatControllerTest {
    private ChatDAO chatDAO;
    private ChatController chatController;

    @BeforeEach
    void setupChatController() {
        chatDAO = mock(ChatDAO.class);
        chatController = new ChatController(chatDAO);
    }

    @Test
    void testGenerateChat() { 
        int userID = 1;
        ChatPersonality chatPersonality = mock(ChatPersonality.class);

        ResponseEntity<Integer> response = chatController.generateChat(userID, chatPersonality);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGenerateChatAlreadyExists() { 
        int userID = 1;
        ChatPersonality chatPersonality = mock(ChatPersonality.class);

        when(chatDAO.chatExists(userID)).thenReturn(true);
        ResponseEntity<Integer> response = chatController.generateChat(userID, chatPersonality);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test 
    void testDeleteChat() {
        int id = 1;

        when(chatDAO.deleteChat(id)).thenReturn(true);
        ResponseEntity<Boolean> response = chatController.deleteChat(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteChatDoesntExist() {
        int id = 1;
        when(chatDAO.deleteChat(id)).thenReturn(false);

        ResponseEntity<Boolean> response = chatController.deleteChat(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSendChatSuccess() {
        String message = "hello";
        int id = 100;

        when(chatDAO.chatExists(id)).thenReturn(true);

        ResponseEntity<String> response = chatController.sendChat(id, message);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSendChatDoesNotExist() {
        String message = "hello";
        int id = 100;

        when(chatDAO.chatExists(id)).thenReturn(false);

        ResponseEntity<String> response = chatController.sendChat(id, message);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetPrompts() {
        ChatPersonality personality = mock(ChatPersonality.class);
        ChatPersonality[] personalities = {personality};

        when(chatDAO.getPersonalities()).thenReturn(personalities);
        assertEquals(HttpStatus.OK, chatController.getPrompts().getStatusCode());
    }

    @Test
    void testGetPromptsNoPrompts() {
        ChatPersonality[] personalities = {};

        when(chatDAO.getPersonalities()).thenReturn(personalities);
        assertEquals(HttpStatus.NO_CONTENT, chatController.getPrompts().getStatusCode());
    }
}

