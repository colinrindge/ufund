package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
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
import com.ufund.api.ufundapi.model.BasketNeed;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.SessionDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;

/**
 * This controller handles api calles for user resources    
 * @author Massimo Malone
 */
@RestController
@RequestMapping("users")
public class UserController {
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());
    private UserDAO userDAO;
    private SessionDAO sessionDAO;

    /**
     * Creates a REST API to respond to request
     * 
     * @param userdao to perform CRUD operations
     */
    public UserController(UserDAO userDAO, SessionDAO sessionDAO){
        this.userDAO = userDAO;
        this.sessionDAO = sessionDAO;
    }

    /**
     * creates a user with provided user object
     * 
     * @param user = The{@link User user} to create
     */
    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user){
        LOG.info("POST /users ");

        try{
            if(!userDAO.userExists(user)) {
                User new_user = userDAO.createUser(user);
                return new ResponseEntity<>(new_user,HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }catch(IOException e){
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * gets a user based off of their id
     * @param id to get the users id
     * returns a user
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id){
        LOG.info("GET /users/"+id);
        try{
            User user = userDAO.getUser(id);
            if(user !=null){
                // Looks for session matching user
                Session session = sessionDAO.getSessionByUser(user.getUserName());
                
                // Checks if either session is valid/authorized or if admin is signed in
                if (sessionDAO.isAuthorized(session, user.getUserName(), true)){
                    return new ResponseEntity<User>(user,HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            }
            else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        catch(IOException e){
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   /**
     * Gets all users
     * 
     * @return Array of all users with HTTP status
     */
    @GetMapping()
    public ResponseEntity<User[]> getAllUsers() {
        LOG.info("GET /users");
        try {
            // Checks if admin is signed in
            if (sessionDAO.isAuthorized(null, null, true)){

                User[] users = userDAO.getAllUsers();
                return new ResponseEntity<>(users, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a user with provided user object
     * 
     * @param id The ID of the user to update
     * @param user The updated {@link User user} data
     * @return The updated user if successful, otherwise 401, 404, 500, or 409
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user) {
        LOG.info("PUT /users/" + id);
        try {
            user = new User(id, user.getUserName(), user.getPassword(), user.getRestricted(), user.getBasket(), user.getSecurity());
            
            Session session = sessionDAO.getSession(id);
            if (sessionDAO.isAuthorized(session, user.getId(), true)){

                User otherUser = userDAO.getUserByName(user.getUserName());
                if(otherUser != null && otherUser.getId() != user.getId()){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                } 

                User updatedUser = userDAO.updateUser(user);
                if (updatedUser != null) {
                    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }  else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } 
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a user by their ID
     * 
     * @param id The ID of the user to delete
     * @return 200 if deleted, 401 if unauthorized, 404 if not found, 500 if error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        LOG.info("DELETE /users/" + id);
        try {
            User user = userDAO.getUser(id);
            Session session = sessionDAO.getSessionByUser(user.getUserName());

            if (sessionDAO.isAuthorized(session, user.getUserName(), true)){

                boolean deleted = userDAO.deleteUser(id);
                if (deleted) {
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets a user by their username
     * 
     * @param username The username of the user
     * @return The user if found, otherwise 401, 404, or 500
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByName(@PathVariable String username) {
        LOG.info("GET /users/username/" + username);
        try {
            User user = userDAO.getUserByName(username);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/basket")
    public ResponseEntity<ArrayList<BasketNeed>> getBasket(@PathVariable int id) {
        LOG.info("GET /user/"+ id + "/basket");
        try {
            Session session = sessionDAO.getSession(id);
            if (sessionDAO.isAuthorized(session, id, false)){
                ArrayList<BasketNeed> basket = userDAO.getBasket(id);
                if (basket != null) {
                    return new ResponseEntity<>(basket, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
            else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/basket")
    public ResponseEntity<User> addNeed(@PathVariable int id, @RequestBody Need need) {
        LOG.info("PUT /user/"+ id + "/basket/" + need);
        try{
            Session session = sessionDAO.getSession(id);
            if (sessionDAO.isAuthorized(session, id, false)){
            User user = userDAO.getUser(id);
                if( user != null) {
                    if(userDAO.needExists(id, need)) {
                        return new ResponseEntity<>(HttpStatus.CONFLICT); 
                    }
                    User updatedUser = userDAO.addNeed(id, need);
                    return new ResponseEntity<User>(updatedUser, HttpStatus.CREATED);
                }
                else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/basket/{count}")
    public ResponseEntity<User> editCount(@PathVariable int id, @PathVariable int count, @RequestBody Need need) {
        LOG.info("PUT /user/"+ id + "/basket/" + count + need);
        try{
            Session session = sessionDAO.getSession(id);
            if (sessionDAO.isAuthorized(session, id, false)){
            User user = userDAO.getUser(id);
                if( user != null) {
                    if(!userDAO.needExists(id, need)) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
                    }
                    User updatedUser = userDAO.editCount(id, need, count);
                    return new ResponseEntity<User>(updatedUser, HttpStatus.CREATED);
                }
                else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@linkplain Need need} from {@linkplain Basket basket} with the given id
     * 
     * @param id The id of the {@link Need need} to be removed
     * 
     * @return ResponseEntity HTTP status of OK if deleted<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @DeleteMapping("/{id}/basket")
    public ResponseEntity<User> removeNeed(@PathVariable int id, @RequestBody Need need) {
        LOG.info("DELETE /user/" + id + "/basket/" + need);
        try{
            Session session = sessionDAO.getSession(id);
            if (sessionDAO.isAuthorized(session, id, false)){
                User user = userDAO.getUser(id);
                if( user != null) {
                    if(!userDAO.needExists(id, need)) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
                    }
                    User updatedUser = userDAO.removeNeed(id, need);
                    return new ResponseEntity<User>(updatedUser, HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}