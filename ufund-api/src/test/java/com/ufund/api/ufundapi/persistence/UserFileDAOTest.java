package com.ufund.api.ufundapi.persistence;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.BasketNeed;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;

@Tag("Persistence-tier")
public class UserFileDAOTest {
    UserFileDAO userFileDAO;
    User[] testUsers;
    ObjectMapper mockObjectMapper;
     /**
     * Before each test, we will create and inject a Mock Object Mapper to
     * isolate the tests from the underlying file
     * @throws IOException
     */
    @BeforeEach
    public void setupUserFileDAO() throws IOException{
        mockObjectMapper = mock(ObjectMapper.class);
        testUsers = new User[3];
        ArrayList<String> security = new ArrayList<String>();
        testUsers[0] = new User(1, "John Dough","1",security);
        testUsers[1] = new User(2, "Jerry Dream","2",security);
        testUsers[2] = new User(3, "Jeff Door","3",security);

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the hero array above
        when(mockObjectMapper
            .readValue(new File("doesnt_matter.txt"),User[].class))
                .thenReturn(testUsers);
        userFileDAO = new UserFileDAO("doesnt_matter.txt",mockObjectMapper);
    }

    @Test
    public void testGetUsers() throws IOException{
        //invoke
        User[] users = userFileDAO.getAllUsers();

        assertEquals(users.length, testUsers.length);
        for(int i = 0; i < testUsers.length;i++){
            assertEquals(users[i], testUsers[i]);
        }
    }
    @Test
    public void testGetUser() throws IOException{
        User user = userFileDAO.getUser(1);
        assertEquals(user, testUsers[0]);
    }

    @Test
    public void testCreateUser() throws IOException{
        //setup
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(4, "John Dior","jnde",security);

        //invoke
        User result = assertDoesNotThrow(() -> userFileDAO.createUser(user),
            "Unexpected exception thrown");
        //analyze
        assertNotNull(result);
        User actual = userFileDAO.getUser(user.getId());
        assertEquals(actual.getId(), user.getId());
        assertEquals(actual.getUserName(), user.getUserName());
    }

    @Test
    public void testGetUserByName()throws IOException{
        User user = userFileDAO.getUserByName("John Dough");
        assertEquals(user, testUsers[0]);
    }

    @Test 
    public void testUpdateUser() throws IOException{
        //setup
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "John Donald","jdmc",security);

        //invoke 
        User result = assertDoesNotThrow(() -> userFileDAO.updateUser(user),
        "Unexpected exception thrown");
        assertNotNull(result);
        User actual = userFileDAO.getUser(1);
        assertEquals(user, actual);
    }
    @Test 
    public void testDeleteUser() throws IOException {
        boolean result = assertDoesNotThrow(()-> userFileDAO.deleteUser(1),
        "Unexpected exception thrown");
        assertEquals(result, true);
        assertEquals(userFileDAO.getAllUsers().length, testUsers.length-1);
    }
    @Test 
    public void testUserExist(){
        boolean result = assertDoesNotThrow(()-> userFileDAO.userExists(testUsers[0]),
        "Unexpected exception thrown");
        assertTrue(result);
    }
    @Test
    public void testUserNotExist(){
        ArrayList<String> security = new ArrayList<String>();
        boolean result = assertDoesNotThrow(()-> userFileDAO.userExists(new User(10,"Jake Fake","dc",security)),
        "Unexpected exception thrown");
        assertFalse(result);
    }

    @Test
    public void testBasket() throws IOException {
        // setup
        User user = testUsers[0];
        Need need1 = new Need(1,"Water", 10, 1,"Food", "liquid");
        Need need = new Need(2,"Water", 10, 1,"Food", "liquid");

        assertTrue(userFileDAO.getBasket(user.getId()).isEmpty());

        userFileDAO.addNeed(user.getId(), need1);
        User updatedUser = assertDoesNotThrow(() -> userFileDAO.addNeed(user.getId(), need),
                "Unexpected exception when adding need");
        assertNotNull(updatedUser);
        assertTrue(userFileDAO.needExists(user.getId(), need));
        assertEquals(2, userFileDAO.getBasket(user.getId()).size());

        userFileDAO.removeNeed(user.getId(), need1);
        User afterRemove = assertDoesNotThrow(() -> userFileDAO.removeNeed(user.getId(), need),
                "Unexpected exception when removing need");
        assertNotNull(afterRemove);
        assertFalse(userFileDAO.needExists(user.getId(), need));
        assertTrue(userFileDAO.getBasket(user.getId()).isEmpty());
    }

    /**
     * Tests that you can edit the count of a need in a users basket
     */
    @Test
    public void testEditCount() throws IOException{
        int id = 1;
        Need need = new Need(1,"Water", 10, 1,"Food", "liquid");
        int count = 2;

        userFileDAO.addNeed(id, need);
        User res = userFileDAO.editCount(id, need, count);
        assertEquals(id, res.getId());
        assertEquals(count, res.getBasket().get(id-1).getCount());
    }

    /**
     * Tests that you cant edit the count of a need in a users basket if the user doesnt exist
     */
    @Test
    public void testEditCountUserDNE() throws IOException{
        int id = 99;
        Need need = new Need(1,"Water", 10, 1,"Food", "liquid");
        int count = 2;

        User res = userFileDAO.editCount(id, need, count);
        assertNull(res);
    }

    /**
     * Tests that you cant remove a need in a users basket if the user doesnt exist
     */
    @Test
    public void testRemoveNeedUserDNE() throws IOException{
        int id = 99;
        Need need = new Need(1,"Water", 10, 1,"Food", "liquid");

        User res = userFileDAO.removeNeed(id, need);
        assertNull(res);
    }

    /**
     * Tests that you can check if a user has a need if the user doesnt exist
     */
    @Test
    public void testNeedExistsUserDNE() throws IOException{
        int id = 99;
        Need need = new Need(1,"Water", 10, 1,"Food", "liquid");

        boolean res = userFileDAO.needExists(id, need);
        assertFalse(res);
    }

    /**
     * Tests that you cant add a need in a users basket if the user doesnt exist
     */
    @Test
    public void testAddNeedUserDNE() throws IOException{
        int id = 99;
        Need need = new Need(1,"Water", 10, 1,"Food", "liquid");

        User res = userFileDAO.addNeed(id, need);
        assertNull(res);
    }

    /**
     * Tests that you cant get users basket if the user doesnt exist
     */
    @Test
    public void testGetBasketUserDNE() throws IOException{
        int id = 99;

        ArrayList<BasketNeed> res = userFileDAO.getBasket(id);
        assertNull(res);
    }

    /**
     * Tests that you cant update a user if the user doesnt exist
     */
    @Test
    public void testUpdateUserDNE() throws IOException{
        int id = 99;
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(id, "im not real","bhn ",security);

        User res = userFileDAO.updateUser(user);
        assertNull(res);
    }

    /**
     * Tests that you cant delete a user if the user doesnt exist
     */
    @Test
    public void testDeleteUserDNE() throws IOException{
        int id = 99;

        boolean res = userFileDAO.deleteUser(id);
        assertFalse(res);
    }  

    /**
     * Tests that you can checkif a user exists if a user with the username doesnt but one with the id does
     */
    @Test
    public void testUserExistsUsernameDNE() throws IOException{
        int id = 1;
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(id, "im not real","jn",security);

        boolean res = userFileDAO.userExists(user);
        assertTrue(res);
    }  

}



