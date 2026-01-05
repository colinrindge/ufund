package com.ufund.api.ufundapi.persistence;

import java.io.IOException;
import java.util.ArrayList;
import com.ufund.api.ufundapi.model.BasketNeed;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;

/**
 * Defines the interface for User object persistance
 */
public interface UserDAO {
    /**
     * creates user if one dosent exist already
     * @throws IOException if an issue with underlying storage
     */
    User createUser(User user) throws IOException;
    
    /**
     * get user by their id
     * @param id
     * @return a User 
     * @throws IOException if an issue with underlying storage
     */
    User getUser(int id) throws IOException;

    /**
     * get the user by their username
     * @param Username
     * @returns a user
     * @throws IOException if an issue with underlying storage
     */
    User getUserByName(String Username) throws IOException;

    /**
     * Retrieves all users.
     * @return An array of all users
     * @throws IOException if there is an issue with file access
     */
    User[] getAllUsers() throws IOException;

    /**
     * Updates and saves a user.
     * @param user The user to update
     * @return The updated user if successful, null if not found
     * @throws IOException if there is an issue with file access
     */
    User updateUser(User user) throws IOException;

    /**
     * Deletes a user by id.
     * @param id The id of the user
     * @return true if the user was deleted, false if not found
     * @throws IOException if there is an issue with file access
     */
    boolean deleteUser(int id) throws IOException;

    /**
     * Checks to see if a user already exists with the same username or ID
     * @param user The user to check for
     * @return true if the user already exists, false otherwise
     * @throws IOException if there is an issue with file access
     */
    boolean userExists(User user) throws IOException;

    /**
     * Retrieves a basket as an ArrayList<Need> with the given id of a user
     * 
     * @param id The user's id of the basket to get
     * 
     * @return the basket as a ArrayList<Need>object with the matching user id
     * 
     * null if there is no user
     * 
     * @throws IOException if an issue with underlying storage
     */
    ArrayList<BasketNeed> getBasket(int id) throws IOException;


    /**
     * Adds a need to the Basket with the given id of a user and a need
     * 
     * @param id The Basket's user ID
     * 
     * @param need The {@link Need need} to add
     * 
     * @return retruns the user with the updated basket ArrayList<Need> after the need's ID was added
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    User addNeed(int id, Need need) throws IOException;

    /**
     * Removes a need to the Basket with the given id of a user and a need
     * 
     * @param id The Basket's user ID
     * 
     * @param need The {@link Need need} to remove
     * 
     * @return returns the user with the updated basket ArrayList<Need> with the removed need
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    User removeNeed(int id, Need need) throws IOException;


    /**
     * Edit a number to the need in a user's Basket with the given id of a user, an amount, and the need
     * 
     * @param id The Basket's user ID
     * 
     * @param need The {@link Need need} to add to
     * 
     * @param int The amount to add
     * 
     * @return returns the user with the updated basket ArrayList<Need> after the need's ID was added
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    User editCount(int id, Need need, int count) throws IOException;

    /**
     * Checks if a need with the specified ID is in the basket already
     * 
     * @param userId The Basket user's ID
     * 
     * @param need The need to check
     * 
     * @return true if a need with the given ID is in the basket, false if the basket or need doesn't exist
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean needExists(int userId, Need need) throws IOException;

}