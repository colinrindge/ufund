package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.model.Session;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.SessionDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;

/**
 * Tests for Session Controller class
 * 
 * @author Colin Rindge
 */
@Tag("Controller-tier")
public class SessionControllerTest {
    private SessionDAO mockSessionDAO;
    private UserDAO mockUserDAO;
    private SessionController sessionController;

    
    /**
     * Before each test, create a new sessionController object and inject
     * a mock User DAO and Session DAO
     */
    @BeforeEach
    public void setupSessionController() {
        mockUserDAO = mock(UserDAO.class);
        mockSessionDAO = mock(SessionDAO.class);
        sessionController = new SessionController(mockUserDAO, mockSessionDAO);
    }

    /**
     * Tests that a user can be logged in
     */
    @Test
    public void testLogin() throws IOException {
        String username = "user";
        int id = 1;
        String password = "a";
    
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getUserName()).thenReturn(username);
        when(user.passwordMatches(password)).thenReturn(true);
    
        Session session = new Session(id, username, 12345);
    
        when(mockUserDAO.getUserByName(username)).thenReturn(user);
        when(mockSessionDAO.createSession(id, username)).thenReturn(session);
    
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);
    
        ResponseEntity<Session> response = sessionController.login(credentials);
    
        assertEquals(HttpStatus.OK, response.getStatusCode()); // note: login returns 200 OK, not CREATED
        assertEquals(session, response.getBody());
    }
    

    /**
     * Tests that a user cant be logged in if the user doesn't exist
     */
    @Test
    public void testLoginUserDNE() throws IOException {
        String username = "user";
        String password = "abc";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        when(mockUserDAO.getUserByName(username)).thenReturn(null);
        ResponseEntity<Session> response = sessionController.login(credentials);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests that a user cant be logged in if an IO Exception is thrown
     */
    @Test
    public void testLoginIOException() throws IOException {
        String username = "user";
        int id = 1;
        String password = "a";
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(id, username,password,security);
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password
    );

        when(mockUserDAO.getUserByName(username)).thenReturn(user);
        doThrow(new IOException()).when(mockSessionDAO).createSession(id, username);
        ResponseEntity<Session> response = sessionController.login(credentials);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * Tests that a user can be logged out
     */
    @Test
    public void testLogout() throws IOException {
        String username = "user";
        int id = 1;
        Session session = new Session(id, username, 12345);

        when(mockSessionDAO.getSessionByUser(username)).thenReturn(session);
        when(mockSessionDAO.deleteSession(id)).thenReturn(session);

        ResponseEntity<Session> response = sessionController.logout(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(session, response.getBody());
    }

    /**
     * Tests that a user cant be logged out if they are not logged in
     */
    @Test
    public void testLogoutSessionDNE() throws IOException {
        String username = "user";

        when(mockSessionDAO.getSessionByUser(username)).thenReturn(null);
        ResponseEntity<Session> response = sessionController.logout(username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests that a user cant be logged out if an IO Exception is thrown
     */
    @Test
    public void testLogoutIOException() throws IOException {
        String username = "user";
        int id = 1;
        Session session = new Session(id, username, 12345);

        when(mockSessionDAO.getSessionByUser(username)).thenReturn(session);
        doThrow(new IOException()).when(mockSessionDAO).deleteSession(id);

        ResponseEntity<Session> response = sessionController.logout(username);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests that a session can be confirmed to be valid
     */
    @Test
    public void testIsValidSession() throws IOException {
        String username = "user";
        int id = 1;
        Session session = new Session(id, username, 12345);

        when(mockSessionDAO.getSessionByUser(username)).thenReturn(session);
        when(mockSessionDAO.isExpired(session)).thenReturn(false);

        ResponseEntity<Boolean> response = sessionController.isValidSession(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    /**
     * Tests that a session can be confirmed to be invalid
     */
    @Test
    public void testIsInvalidSession() throws IOException {
        String username = "user";
        int id = 1;
        Session session = new Session(id, username, 12345);

        when(mockSessionDAO.getSessionByUser(username)).thenReturn(session);
        when(mockSessionDAO.isExpired(session)).thenReturn(true);

        ResponseEntity<Boolean> response = sessionController.isValidSession(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    /**
     * Tests that a session cant be confirmed to be valid if the session doesnt exist
     */
    @Test
    public void testIsValidSessionDNE() throws IOException {
        String username = "user";

        when(mockSessionDAO.getSessionByUser(username)).thenReturn(null);
        ResponseEntity<Boolean> response = sessionController.isValidSession(username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests that a session cant be confirmed to be valid if an IO exception is thrown
     */
    @Test
    public void testIsValidSessionIOException() throws IOException {
        String username = "user";
        int id = 1;
        Session session = new Session(id, username, 12345);

        when(mockSessionDAO.getSessionByUser(username)).thenReturn(session);
        doThrow(new IOException()).when(mockSessionDAO).isExpired(session);
        ResponseEntity<Boolean> response = sessionController.isValidSession(username);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests that a session can be validated
     */
    @Test
    public void testValidateSession() throws IOException {
        String username = "user";
        int id = 1;
        String password = "a";
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(id, username,password,security);
        Session session = new Session(id, username, 12345);

        when(mockUserDAO.getUserByName(username)).thenReturn(user);
        when(mockSessionDAO.createSession(id, username)).thenReturn(session);
        when(mockSessionDAO.updateSession(session)).thenReturn(session);

        ResponseEntity<Session> response = sessionController.validateSession(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(session, response.getBody());
    }

    /**
     * Tests that a session cant be validated if the user doesn't exist
     */
    @Test
    public void testValidateSessionUserDNE() throws IOException {
        String username = "user";

        when(mockUserDAO.getUserByName(username)).thenReturn(null);

        ResponseEntity<Session> response = sessionController.validateSession(username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests that a session cant be validated whne an IO Exception is thrown
     */
    @Test
    public void testValidateSessionIOException() throws IOException {
        String username = "user";
        int id = 1;
        String password = "a";
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(id, username,password,security);
        Session session = new Session(id, username, 12345);

        when(mockUserDAO.getUserByName(username)).thenReturn(user);
        when(mockSessionDAO.createSession(id, username)).thenReturn(session);
        doThrow(new IOException()).when(mockSessionDAO).updateSession(session);

        ResponseEntity<Session> response = sessionController.validateSession(username);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}