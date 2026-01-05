package com.ufund.api.ufundapi.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ufund.api.ufundapi.model.ChatPersonality;
import com.ufund.api.ufundapi.persistence.ChatDAO;
import com.ufund.api.ufundapi.model.User;

/**
 * Handles the REST API requests for the Need resource
 * 
 * @author Kyle Long
 */

@RestController

@RequestMapping("chat")
public class ChatController {
    private ChatDAO chatDAO;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param chatDAO The {@link ChatDAO Chat Data Access Object} to perform CRUD operations
     */
    public ChatController(ChatDAO chatDAO) {
        this.chatDAO = chatDAO;
    }

    /**
     * Gets the {@linkplain ChatPersonality personalities} array
     * 
     * @return ResponseEntity with created {@link ChatPersonality personailites} Array and HTTP status of OK.<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/personalities")
    public ResponseEntity<ChatPersonality[]> getPrompts() {
        ChatPersonality[] personalities = chatDAO.getPersonalities();
        if (personalities.length > 0) {
            return new ResponseEntity<>(personalities, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } 

    /**
     * Creates a {@linkplain Chat chat} instance
     * 
     * @param id The {@linkplain User userID} of the user attempting to create the chat
     * @param personality The {@linkplain ChatPersonality personality} object selected by the user
     * 
     * @return ResponseEntity with created {@link String response} and HTTP status of OK.<br>
     * ResponseEntity with HTTP status of CONFLICT if chat already exists with given user ID
     */
    @PostMapping("/{id}")
    public ResponseEntity<Integer> generateChat(@PathVariable int id, @RequestBody ChatPersonality personality) {
        if (chatDAO.chatExists(id)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Integer response = chatDAO.generateChat(id, personality);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Submits a {@linkplain String message} to the users {@linkplain Chat chat} instance
     * 
     * @param id The {@linkplain User userID} of the user attempting to send the chat
     * @param personality The {@linkplain String message} sent by the user
     * 
     * @return ResponseEntity with corresponding {@linkplain String response message} and HTTP status of OK.<br>
     * ResponseEntity with HTTP status of NOT_FOUND if chat with the submitted {@linkplain User userID} does not exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> sendChat(@PathVariable int id, @RequestBody String messsage) {
        if (chatDAO.chatExists(id)) {
            String response = chatDAO.submitChat(id, messsage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    /**
     * Deletes a {@linkplain Chat chat} instance
     * 
     * @param id The {@linkplain User userID} of the user whos chat is being deleted
     * 
     * @return ResponseEntity with HTTP status of OK if deleted.<br>
     * ResponseEntity with HTTP status of NOT_FOUND if chat with the submitted {@linkplain User userID} does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteChat(@PathVariable int id) { 
        boolean result = chatDAO.deleteChat(id);
        if (result == true) {
            return new ResponseEntity<>(true,HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }

    /**
     * Determines whether a {@linkplain Chat chat} instance exists for a given {@linkplain User userID}
     * 
     * @param id The {@linkplain User userID} of the requested User
     * 
     * @return ResponseEntity with HTTP status of OK and boolean of value true if chat is found, false if otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Boolean> chatExists(@PathVariable int id) {
        boolean result = chatDAO.chatExists(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
