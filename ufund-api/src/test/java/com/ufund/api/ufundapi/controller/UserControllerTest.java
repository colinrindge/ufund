package com.ufund.api.ufundapi.controller;

import com.ufund.api.ufundapi.persistence.UserDAO;
import com.ufund.api.ufundapi.persistence.SessionDAO;
import com.ufund.api.ufundapi.model.BasketNeed;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.Session;
import com.ufund.api.ufundapi.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Test the User Controller class
 * 
 * @author Timothy Steffens
 */
@Tag("Controller-tier")
public class UserControllerTest {
    private UserController userController;
    private UserDAO mockUserDAO;
    private SessionDAO mockSessionDAO;

    /**
     * Before each test, create a new userController object and inject
     * a mock User DAO
     */
    @BeforeEach
    public void setupUserController() {
        mockUserDAO = mock(UserDAO.class);
        mockSessionDAO = mock(SessionDAO.class);
        userController = new UserController(mockUserDAO, mockSessionDAO);
    }
    
    /**
     * Tests if a user can be successfully created
     */
    @Test
    public void testCreateUser() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","abc",security);

        when(mockUserDAO.createUser(user)).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

     /**
     * Tests that a conflict is thrown when a user already exists
     */
    @Test
    public void testCreateUserConflict() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","abc",security);

        when(mockUserDAO.createUser(user)).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(true);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * Tests that a user is not created if an IO Exception is thrown
     */
    @Test
    public void testCreateUserIOException() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","abc",security);

        when(mockUserDAO.createUser(user)).thenReturn(user);
        doThrow(new IOException()).when(mockUserDAO).createUser(user);
        
        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests if a user can be get
     */
    @Test
    public void testGetUser() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","abc",security);

        when(mockSessionDAO.isAuthorized(null, user.getUserName(), true)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);

        ResponseEntity<User> response = userController.getUser(user.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    /**
     * Tests if a user can't be get when unauthorized
     */
    @Test
    public void testGetUserUnauthorized() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","abc",security);

        when(mockSessionDAO.isAuthorized(null, "notJohnDoe", true)).thenReturn(false);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);

        ResponseEntity<User> response = userController.getUser(user.getId());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests if a user can't be get when not found
     */
    @Test
    public void testGetUserNotFound() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","abc",security);

        when(mockSessionDAO.isAuthorized(null, user.getUserName(), true)).thenReturn(true);

        when(mockUserDAO.getUser(2)).thenReturn(null);

        ResponseEntity<User> response = userController.getUser(user.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests that a user is not gotten if an IO Exception is thrown
     */
    @Test
    public void testGetUserIOException() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","abc",security);

        when(mockSessionDAO.isAuthorized(null, user.getUserName(), true)).thenReturn(true);

        doThrow(new IOException()).when(mockUserDAO).getUser(user.getId());

        ResponseEntity<User> response = userController.getUser(user.getId());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests if all users can be get
     */
    @Test
    public void testGetAllUsers() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user1 = new User(1, "JohnDoe","password",security);
        User user2 = new User(2, "JohnDough","hoid",security);
        User[] users = {user1, user2};

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);

        when(mockUserDAO.getAllUsers()).thenReturn(users);

        ResponseEntity<User[]> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    /**
     * Tests that all users can not be gotten if an IO Exception is thrown
     */
    @Test
    public void testGetAllUsersIOException() throws IOException {
        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);

        doThrow(new IOException()).when(mockUserDAO).getAllUsers();

        ResponseEntity<User[]> response = userController.getAllUsers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests if all users can't be get when not authorized
     */
    @Test
    public void testGetAllUsersUnauthorized() throws IOException {

        when(mockSessionDAO.isAuthorized(null, null, false)).thenReturn(false);

        ResponseEntity<User[]> response = userController.getAllUsers();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests if all users can be get when empty
     */
    @Test
    public void testGetAllUsersEmpty() throws IOException {
        User[] users = {};

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);

        when(mockUserDAO.getAllUsers()).thenReturn(users);

        ResponseEntity<User[]> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    /**
     * Tests that an existing user can be updated
     */
    @Test
    public void testUpdateUser() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(2, "JohnDoe","hefhuidj",security);
        User new_user = new User(2, "Jeff","Kill joe biden",security);
        Session session = new Session(new_user.getId(), new_user.getUserName(), 100);

        when(mockSessionDAO.getSession(new_user.getId())).thenReturn(session);
        when(mockSessionDAO.isAuthorized(session, new_user.getId(), true)).thenReturn(true);        
        when(mockUserDAO.getUserByName(user.getUserName())).thenReturn(user);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(true);
        when(mockUserDAO.updateUser(any())).thenAnswer(returnsFirstArg());

        ResponseEntity<User> response = userController.updateUser(user.getId(), new_user);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new_user.getId(), response.getBody().getId());
        assertEquals(new_user.getUserName(), response.getBody().getUserName());
    }

    /**
     * Tests that an existing user can't be updated if user with id doesnt exist
     */
    @Test
    public void testUpdateUserDNE() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(100, "JohnDoe","meow",security);
        User new_user = new User(2, "Jeff","hufewjnd",security);
        Session session = new Session(new_user.getId(), new_user.getUserName(), 100);

        when(mockSessionDAO.getSession(new_user.getId())).thenReturn(session);
        when(mockSessionDAO.isAuthorized(session, new_user.getId(),true)).thenReturn(true);        
        when(mockUserDAO.getUserByName(anyString())).thenReturn(user);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(true);
        when(mockUserDAO.updateUser(any())).thenAnswer(returnsFirstArg());

        ResponseEntity<User> response = userController.updateUser(2, new_user);


        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * Tests that an existing user can't be updated if an IO Exception is thrown
     */
    @Test
    public void testUpdateUserIOException() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(2, "JohnDoe","j",security);
        User new_user = new User(2, "Jeff","ejnd",security);
        Session session = new Session(new_user.getId(), new_user.getUserName(), 100);

        when(mockSessionDAO.getSession(new_user.getId())).thenReturn(session);
        when(mockSessionDAO.isAuthorized(session, new_user.getId(), true)).thenReturn(true);        
        when(mockUserDAO.getUserByName(user.getUserName())).thenReturn(user);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(true);
        when(mockUserDAO.updateUser(any())).thenAnswer(returnsFirstArg());
        doThrow(new IOException()).when(mockUserDAO).updateUser(any());

        ResponseEntity<User> response = userController.updateUser(user.getId(), new_user);


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests that an existing user can't be updated when not authorized
     */
    @Test
    public void testUpdateUserUnauthorized() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","neiwok",security);

        when(mockSessionDAO.isAuthorized(null, "NotJohnDoe", true)).thenReturn(false);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(true);
        when(mockUserDAO.updateUser(user)).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser(user.getId(), user);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests that a user not found can't be updated 
     */
    @Test
    public void testUpdateUserNotFound() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","hn",security);

        when(mockSessionDAO.isAuthorized(null, user.getId(), true)).thenReturn(true);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(false);
        when(mockUserDAO.updateUser(user)).thenReturn(null);

        ResponseEntity<User> response = userController.updateUser(1, user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests that an existing user can be deleted
     */
    @Test
    public void testDeleteUser() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","hiuewjdncs",security);

        when(mockSessionDAO.isAuthorized(null, user.getUserName(), true)).thenReturn(true);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(true);
        when(mockUserDAO.deleteUser(user.getId())).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUser(user.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Tests that an existing user can't be deleted when not authorized
     */
    @Test
    public void testDeleteUserUnauthorized() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","hub",security);

        when(mockSessionDAO.isAuthorized(null, "NotJohnDoe", true)).thenReturn(false);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(true);
        when(mockUserDAO.deleteUser(user.getId())).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUser(user.getId());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * Tests that a user not found can't be deleted 
     */
    @Test
    public void testDeleteUserNotFound() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","bfhdcn",security);

        when(mockSessionDAO.isAuthorized(null, user.getUserName(), true)).thenReturn(true);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(false);
        when(mockUserDAO.deleteUser(user.getId())).thenReturn(false);

        ResponseEntity<Void> response = userController.deleteUser(user.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests that an existing user can't be deleted when an IO Exception is thrown
     */
    @Test
    public void testDeleteUserIOException() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","edhbs",security);

        when(mockSessionDAO.isAuthorized(null, user.getUserName(), true)).thenReturn(true);
        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.userExists(user)).thenReturn(true);
        doThrow(new IOException()).when(mockUserDAO).deleteUser(user.getId());

        ResponseEntity<Void> response = userController.deleteUser(user.getId());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests if a user can be get by name
     */
    @Test
    public void testGetUserByName() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","hedbs",security);

        when(mockSessionDAO.isAuthorized(null, user.getUserName(), true)).thenReturn(true);

        when(mockUserDAO.getUserByName(user.getUserName())).thenReturn(user);

        ResponseEntity<User> response = userController.getUserByName(user.getUserName());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }


    /**
     * Tests if a user can't be get by name when not found
     */
    @Test
    public void testGetUserByNameNotFound() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","huecdjn",security);

        when(mockSessionDAO.isAuthorized(null, user.getUserName(), true)).thenReturn(true);

        when(mockUserDAO.getUserByName("NotJohnDoe")).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByName(user.getUserName());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests that a user can not be gotten by name if an IO Exception is thrown
     */
    @Test
    public void testGetUserByNameIOException() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","kl;m.",security);

        when(mockSessionDAO.isAuthorized(null, user.getUserName(), true)).thenReturn(true);

        when(mockUserDAO.getUserByName(user.getUserName())).thenReturn(user);
        doThrow(new IOException()).when(mockUserDAO).getUserByName(user.getUserName());

        ResponseEntity<User> response = userController.getUserByName(user.getUserName());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests if a user's basket can be get
     */
    @Test
    public void testGetBasket() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","hijn",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());

        ResponseEntity<ArrayList<BasketNeed>> response = userController.getBasket(user.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getBasket(), response.getBody());
    }

    /**
     * Tests if a user's basket can't be get when unauthorized
     */
    @Test
    public void testGetBasketUnauthorized() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","j",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);

        when(mockSessionDAO.isAuthorized(null, 2, false)).thenReturn(false);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());

        ResponseEntity<ArrayList<BasketNeed>> response = userController.getBasket(user.getId());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests if a user's basket can't be get when not found
     */
    @Test
    public void testGetBasketNotFound() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","kol",security);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(null);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(null);

        ResponseEntity<ArrayList<BasketNeed>> response = userController.getBasket(user.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests tgat a user's basket cant be get if an IO Exception is thrown
     */
    @Test
    public void testGetBasketIOException() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","jm",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        doThrow(new IOException()).when(mockUserDAO).getBasket(user.getId());

        ResponseEntity<ArrayList<BasketNeed>> response = userController.getBasket(user.getId());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests if a user's basket can have need's added
     */
    @Test
    public void testAddNeed() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","jn",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);

        Need need2 = new Need(1, null, 0, 0, null, null);
        user.addNeed(need2);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.addNeed(user.getId(), need2)).thenReturn(user);

        ResponseEntity<User> response = userController.addNeed(user.getId(), need2);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    /**
     * Tests if a user's basket can't be added with a need when unauthorized
     */
    @Test
    public void testAddNeedUnauthorized() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","jnm",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);

        Need need2 = new Need(1, null, 0, 0, null, null);
        user.addNeed(need2);

        when(mockSessionDAO.isAuthorized(null, 2, true)).thenReturn(false);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.addNeed(user.getId(), need2)).thenReturn(user);

        ResponseEntity<User> response = userController.addNeed(user.getId(), need2);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests if a adding a need to a user's basket that was not found
     */
    @Test
    public void testAddNeedNotFound() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","jun",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);

        Need need2 = new Need(1, null, 0, 0, null, null);
        user.addNeed(need2);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(null);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(null);
        when(mockUserDAO.addNeed(user.getId(), need2)).thenReturn(null);

        ResponseEntity<User> response = userController.addNeed(user.getId(), need2);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests if a adding a need to a user's basket that was not found
     */
    @Test
    public void testAddNeedConflict() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","huijn",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(true);

        ResponseEntity<User> response = userController.addNeed(user.getId(), need);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests that a user's basket cant have need's added if an IO exception is thrown
     */
    @Test
    public void testAddNeedIOException() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","jkn",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);

        Need need2 = new Need(1, null, 0, 0, null, null);
        user.addNeed(need2);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        doThrow(new IOException()).when(mockUserDAO).addNeed(user.getId(), need2);

        ResponseEntity<User> response = userController.addNeed(user.getId(), need2);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests if a user's basket can have needs' counts edited
     */
    @Test
    public void testEditCount() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","bhn",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);
        BasketNeed basketNeed = user.getBasketNeed(need);
        basketNeed.editCount(1);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(true);
        when(mockUserDAO.editCount(user.getId(), need, 1)).thenReturn(user);

        ResponseEntity<User> response = userController.editCount(user.getId(), 1, need);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    /**
     * Tests that a user's basket cant have needs' counts edited if the need doesnt exist
     */
    @Test
    public void testEditCountNeedDNE() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","JHUNM",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);
        BasketNeed basketNeed = user.getBasketNeed(need);
        basketNeed.editCount(1);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(false);
        when(mockUserDAO.editCount(user.getId(), need, 1)).thenReturn(user);

        ResponseEntity<User> response = userController.editCount(user.getId(), 1, need);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests if a user's basket can't have it's needs' counts edited when unauthorized
     */
    @Test
    public void testEditCountUnauthorized() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","HJN",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);
        BasketNeed basketNeed = user.getBasketNeed(need);
        basketNeed.editCount(1);

        when(mockSessionDAO.isAuthorized(null, 2, true)).thenReturn(false);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(true);
        when(mockUserDAO.editCount(user.getId(), need, 1)).thenReturn(user);

        ResponseEntity<User> response = userController.editCount(user.getId(), 1, need);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests if a editing a needs' counts in a user's basket that was not found
     */
    @Test
    public void testEditCountNotFound() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","ihjn",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);
        BasketNeed basketNeed = user.getBasketNeed(need);
        basketNeed.editCount(1);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(null);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(null);
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(true);
        when(mockUserDAO.editCount(user.getId(), need, 1)).thenReturn(null);

        ResponseEntity<User> response = userController.editCount(user.getId(), 1, need);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests that a user's basket cant have needs' counts edited if an IO exception is thrown
     */
    @Test
    public void testEditCountIOException() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","jdn",security);
        Need need = new Need(0, null, 0, 0, null, null);
        user.addNeed(need);
        BasketNeed basketNeed = user.getBasketNeed(need);
        basketNeed.editCount(1);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(true);
        doThrow(new IOException()).when(mockUserDAO).editCount(user.getId(), need, 1);

        ResponseEntity<User> response = userController.editCount(user.getId(), 1, need);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    
    /**
     * Tests if a user's basket can have need's removed
     */
    @Test
    public void testRemoveNeed() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","jkm",security);
        Need need = new Need(0, null, 0, 0, null, null);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(true);
        when(mockUserDAO.removeNeed(user.getId(), need)).thenReturn(user);

        ResponseEntity<User> response = userController.removeNeed(user.getId(), need);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }



    /**
     * Tests if a user's basket can't be removed when unauthorized
     */
    @Test
    public void testRemoveNeedUnauthorized() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","jn",security);
        Need need = new Need(0, null, 0, 0, null, null);

        when(mockSessionDAO.isAuthorized(null, 2, true)).thenReturn(false);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(true);
        when(mockUserDAO.removeNeed(user.getId(), need)).thenReturn(user);

        ResponseEntity<User> response = userController.removeNeed(user.getId(), need);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests if a adding a need to a user's basket that was not found
     */
    @Test
    public void testRemoveNeedNotFound() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","jn",security);
        Need need = new Need(0, null, 0, 0, null, null);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(null);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(null);
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(false);

        ResponseEntity<User> response = userController.removeNeed(user.getId(), need);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    /**
     * Tests if a adding a need to a user's basket that was not found
     */
    @Test
    public void testRemoveNeedNotFound2() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","hbjnm",security);
        Need need = new Need(0, null, 0, 0, null, null);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(false);
        when(mockUserDAO.removeNeed(user.getId(), need)).thenReturn(user);

        ResponseEntity<User> response = userController.removeNeed(user.getId(), need);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests that a user's basket cant have need's removed if an IO exception is thrown
     */
    @Test
    public void testRemoveNeedIOException() throws IOException {
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(1, "JohnDoe","hjnm",security);
        Need need = new Need(0, null, 0, 0, null, null);

        when(mockSessionDAO.isAuthorized(null, user.getId(), false)).thenReturn(true);

        when(mockUserDAO.getUser(user.getId())).thenReturn(user);
        when(mockUserDAO.getBasket(user.getId())).thenReturn(user.getBasket());
        when(mockUserDAO.needExists(user.getId(), need)).thenReturn(true);
        doThrow(new IOException()).when(mockUserDAO).removeNeed(user.getId(), need);

        ResponseEntity<User> response = userController.removeNeed(user.getId(), need);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    
}