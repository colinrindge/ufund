package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
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

import com.ufund.api.ufundapi.model.Session;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.SessionDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;

/**
 * Controller handling api calls for authentication
 */
@RestController
@RequestMapping("auth")
public class SessionController {
    private static final Logger LOG = Logger.getLogger(SessionController.class.getName());
    private UserDAO userDAO;
    private SessionDAO sessionDAO;

    /**
     * Creates the SessionController for REST API responses
     * @param userDAO to perform CRUD operations on users
     * @param sessionDAO to perform CRUD operations on Sessions
     */
    public SessionController(UserDAO userDAO, SessionDAO sessionDAO){
        this.userDAO = userDAO;
        this.sessionDAO = sessionDAO;
    }

    /**
     * Creates a {@link Session session} linked to the given user, if the user exists
     * @param user The {@link User user} to link with the created session
     * @return The session, if successful
     */
    @PostMapping("/login")
    public ResponseEntity<Session> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            User user = userDAO.getUserByName(username);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (user.passwordMatches(password) == true) {
                    Session session = sessionDAO.createSession(user.getId(),user.getUserName());
                    return ResponseEntity.ok(session);
                }
                else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            } catch (IOException e) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a {@link Session session} linked to the given user, if the user exists, by an already hashed password
     * @param user The {@link User user} to link with the created session
     * @return The session, if successful
     */
    @PostMapping("/login/hash")
    public ResponseEntity<Session> loginHash(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            User user = userDAO.getUserByName(username);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (user.hashMatches(password) == true) {
                    Session session = sessionDAO.createSession(user.getId(),user.getUserName());
                    return ResponseEntity.ok(session);
                }
                else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            } catch (IOException e) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@link Session session} by username
     * @param username the username to search for
     * @return the deleted session, if successful
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<Session> logout(@PathVariable String username){
        LOG.info("DELETE /auth/"+username);

        try{
            Session session = sessionDAO.getSessionByUser(username);
            if (session != null) {
                sessionDAO.deleteSession(session.getId());
                return new ResponseEntity<Session>(session, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checks if the {@link Session session} has expired yet
     * @param username the username to search by
     * @return True if still valid, False if expired
     */
    @GetMapping("/{username}")
    public ResponseEntity<Boolean> isValidSession(@PathVariable String username){
        LOG.info("GET /auth/"+username);

        try {
            Session session = sessionDAO.getSessionByUser(username);
            if (session != null){
                return new ResponseEntity<Boolean>(!sessionDAO.isExpired(session), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Validates the {@link Session session}, refreshing its timer
     * @param username the username of the session to refresh
     * @return the updated session, if successful
     */
    @PutMapping("/{username}")
    public ResponseEntity<Session> validateSession(@PathVariable String username){
        LOG.info("PUT /auth/"+username);

        try {  
            User user = userDAO.getUserByName(username);
            if (user != null){          
                int id = user.getId();
                Session newSession = sessionDAO.createSession(id, username);
                sessionDAO.updateSession(newSession);
                return new ResponseEntity<Session>(newSession, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
