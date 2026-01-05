package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.BasketNeed;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.util.Passwordhasher;

@Component
public class UserFileDAO implements UserDAO {

     Map<Integer,User> users;   // Provides a local cache of the User objects
                                // so that we don't need to read from the file
                                // each time
    private ObjectMapper objectMapper;  // Provides conversion between User
                                        // objects and JSON text format written
                                        // to the file
    private static int nextId;  // The next Id to assign to a new User
    private String filename;    // Filename to read from and write to

    /**
     * Creates a User file data object
     * 
     * @param filename the user.json is the file to read and write from
     * @param objectMapper Provides JSON Object to/from Java Object serialization and deserialization
     * 
     */
    public UserFileDAO(@Value("${users.file}") String filename,ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load();  // load the Users from the file
    }

    /**
     * Generates the next id for a new {@linkplain User user}
     * 
     * @return The next id
     */
    private synchronized static int nextId() {
        int id = nextId;
        ++nextId;
        return id;
    }

    /**
     * Generates an array of {@linkplain User users} from the tree map
     * 
     * @return  The array of {@link User users}, may be empty
     */
    private User[] getUsersArray() {
        return getUsersArray(null);
    }

    /**
     * Generates an array of {@linkplain User users} from the tree map for any
     * {@linkplain User users} that contains the text specified by containsText
     * <br>
     * If containsText is null, the array contains all of the {@linkplain User users}
     * in the tree map
     * 
     * @return  The array of {@link User users}, may be empty
     */
    private User[] getUsersArray(String containsText) { // if containsText == null, no filter
        ArrayList<User> userArrayList = new ArrayList<>();

        for (User user : users.values()) {
            if (containsText == null || user.getUserName().contains(containsText)) {
                userArrayList.add(user);
            }
        }

        User[] userArray = new User[userArrayList.size()];
        userArrayList.toArray(userArray);
        return userArray;
    }

     /**
     * Saves the {@linkplain User users} from the map into the file as an array of JSON objects
     * 
     * @return true if the {@link User users} were written successfully
     * 
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        User[] userArray = getUsersArray();

        // Serializes the Java Objects to JSON objects into the file
        // writeValue will thrown an IOException if there is an issue
        // with the file or reading from the file
        objectMapper.writeValue(new File(filename),userArray);
        return true;
    }

    /**
     * loads users from JSON file into the map
     * increments the id
     * @return true if the file was read successfully
     * @throws IOException when file cannot be accessed or read from
     */
    private boolean load() throws IOException{
        users = new TreeMap<>();
        nextId = 0;

         // Deserializes the JSON objects from the file into an array of users
        // readValue will throw an IOException if there's an issue with the file
        // or reading from the file
        User[] userArray = objectMapper.readValue(new File(filename), User[].class);

        //add each user to the map and keep track of greatest id
        for(User user : userArray){
            users.put(user.getId(), user);
            if(user.getId() > nextId){
                nextId = user.getId();
            }
        }
        nextId++;
        return true;
    }


    @Override
    public User createUser(User user) throws IOException {
        synchronized (users) {
            User newUser = new User(nextId(), user.getUserName(), Passwordhasher.hashPassword(user.getPassword()), user.getSecurity());
            users.put(newUser.getId(), newUser);
            save(); 
            return newUser;
        }
    }

    @Override
    public User getUser(int id) throws IOException {
        synchronized (users) {
            return users.get(id); 
        }
    }

    @Override
    public User getUserByName(String userName) throws IOException {
        synchronized (users) {
            for (User user : users.values()) {
                if (user.getUserName().equals(userName)) {
                    return user;
                }
            }
            return null;
        }
    }

    @Override
    public User[] getAllUsers() throws IOException {
        synchronized (users) {
            return getUsersArray();
        }
    }

    @Override
    public User updateUser(User user) throws IOException {
        synchronized (users) {
            if (!users.containsKey(user.getId())) {
                return null; 
            }

            if(user.getPassword().equals("")){
                user.setPasswordNoHash(getUserByName(user.getUserName()).getPassword());
            } else {
                user.setPassword(user.getPassword());
            }

            users.put(user.getId(), user);
            save();
            return user;
        }
    }

    @Override
    public boolean deleteUser(int id) throws IOException {
        synchronized (users) {
            if (users.containsKey(id)) {
                users.remove(id);
                save();
                return true;
            }
            return false;
        }
    }

    public boolean userExists(User user) throws IOException {
        if(getUserByName(user.getUserName()) != null){
            return true;
        }
        if(getUser(user.getId()) != null){
            return true;
        }

        return false;
    }


    /**
    ** {@inheritDoc}
     */
    @Override
    public ArrayList<BasketNeed> getBasket(int id) throws IOException{
        synchronized(users) {
            if (users.get(id) != null){
                User user = users.get(id);
                return user.getBasket();
            }
            return null;
        }
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public User addNeed(int id, Need need) throws IOException {
        synchronized(users) {
            if (users.get(id) == null){
                return null;
            }
            User user = users.get(id);
            user.addNeed(need);
            save(); // may throw an IOException
            return user;
        }
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public User removeNeed(int id, Need need) throws IOException {
        synchronized(users) {
            if (users.get(id) == null){
                return null;
            }
            User user = users.get(id);
            user.removeNeed(need);

            users.put(user.getId(),user);
            save(); // may throw an IOException
            return user;
        }
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public User editCount(int id, Need need, int count) throws IOException {
        synchronized(users) {
            if (users.get(id) == null){
                return null;
            }
            User user = users.get(id);
            BasketNeed basketNeed = user.getBasketNeed(need);
            basketNeed.editCount(count);

            users.put(user.getId(),user);
            save(); // may throw an IOException
            return user;
        }
    }

    /**
    ** {@inheritDoc}
    */
    @Override
    public boolean needExists(int userId, Need need) throws IOException {
        User user = users.get(userId);
        if (user == null) return false;

        ArrayList<BasketNeed> basket = user.getBasket();
        for (BasketNeed b : basket) {
            if (b.getNeed() != null && b.getNeed().getId() == need.getId()) {
                return true;
            }
        }
        return false;
    }
}
