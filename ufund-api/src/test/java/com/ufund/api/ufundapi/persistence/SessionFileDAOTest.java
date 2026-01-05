package com.ufund.api.ufundapi.persistence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Session;

/**
 * Test the Session File DAO Class
 * 
 * @author Colin Rindge
 */
@Tag("Persistence-Tier")
public class SessionFileDAOTest {

    private ObjectMapper mockObjectMapper;
    private SessionFileDAO sessionFileDAO;
    Session[] testSessions;
    String filename = "filename";

    /**
     * Before each test, we will create and inject a Mock Object Mapper to
     * isolate the tests from the underlying file
     * @throws IOException
     */
    @BeforeEach
    public void setupSessionFileDAO() throws IOException{
        mockObjectMapper = mock(ObjectMapper.class);
        testSessions = new Session[3];
        testSessions[0] = new Session(1, "user1", java.lang.System.currentTimeMillis() + 9999999);
        testSessions[1] = new Session(2, "user2", java.lang.System.currentTimeMillis() + 9999999);
        testSessions[2] = new Session(3, "admin", java.lang.System.currentTimeMillis() + 9999999);

        when(mockObjectMapper
            .readValue(new File(filename),Session[].class))
                .thenReturn(testSessions);

        this.sessionFileDAO = new SessionFileDAO(filename, mockObjectMapper);
    }

    /**
     * Tests that a session can be created
     */
    @Test
    public void testCreateSession() throws IOException{
        int id = 99;
        String username = "test";
        Session res = sessionFileDAO.createSession(id, username);
        assertEquals(id, res.getId());
        assertEquals(username, res.getUserName());
    }

    /**
     * Tests that a session cant be created if the username passed is null
     */
    @Test
    public void testCreateSessionNullUser() throws IOException{
        int id = 99;
        Session res = sessionFileDAO.createSession(id, null);
        assertNull(res);
    }

    /**
     * Tests that a session can be gotten
     */
    @Test
    public void testGetSession() throws IOException{
        int id = 1;
        Session res = sessionFileDAO.getSession(id);
        assertEquals(id, res.getId());
    }

    /**
     * Tests that a non existant session can be gotten
     */
    @Test
    public void testGetSessionNull() throws IOException{
        int id = 99;
        Session res = sessionFileDAO.getSession(id);
        assertNull(res);
    }

    /**
     * Tests that a session can be gotten by username
     */
    @Test
    public void testGetSessionByUser() throws IOException{
        int id = 3;
        String username = "admin";
        Session res = sessionFileDAO.getSessionByUser(username);
        assertEquals(id, res.getId());
        assertEquals(username, res.getUserName());
    }

    /**
     * Tests that a session cant be gotten by username if no sessions exist
     */
    @Test
    public void testGetSessionByUserNoSessions() throws IOException{
        mockObjectMapper = mock(ObjectMapper.class);
        testSessions = new Session[0];

        when(mockObjectMapper
            .readValue(new File(filename),Session[].class))
                .thenReturn(testSessions);

        this.sessionFileDAO = new SessionFileDAO(filename, mockObjectMapper);
        
        String username = "user1";
        Session res = sessionFileDAO.getSessionByUser(username);
        assertNull(res);
    }

    /**
     * Tests that a session cant be gotten by username if the username is null 
     */
    @Test
    public void testGetSessionByUserNullUsername() throws IOException{
        mockObjectMapper = mock(ObjectMapper.class);
        testSessions = new Session[1];
        testSessions[0] = new Session(1, null, 10000);

        when(mockObjectMapper
            .readValue(new File(filename),Session[].class))
                .thenReturn(testSessions);

        this.sessionFileDAO = new SessionFileDAO(filename, mockObjectMapper);
        
        String username = "user1";
        Session res = sessionFileDAO.getSessionByUser(username);
        assertNull(res);
    }

    /**
     * Tests that a session can be updated
     */
    @Test
    public void testUpdateSession() throws IOException{
        int id = 2;
        String username = "user10";
        Session testSession = new Session(id, username, 99);
        Session res = sessionFileDAO.updateSession(testSession);
        assertEquals(id, res.getId());
        assertEquals(username, res.getUserName());
    }

    /**
     * Tests that a session cant be updated if it doesnt exist
     */
    @Test
    public void testUpdateSessionDNE() throws IOException{
        int id = 99;
        String username = "user10";
        Session testSession = new Session(id, username, 99);
        Session res = sessionFileDAO.updateSession(testSession);
        assertNull(res);
    }

    /**
     * Tests that a session can be deleted
     */
    @Test
    public void testDeleteSession() throws IOException{
        int id = 2;
        Session res = sessionFileDAO.deleteSession(id);
        assertEquals(id, res.getId());
    }

    /**
     * Tests that you can check if a test is expired
     */
    @Test
    public void testIsExpired() throws IOException{
        int id = 2;
        String username = "user2";
        long startTime = 0;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isExpired(testSession);
        assertTrue(res);
    }

    /**
     * Tests that you can check if a session is not expired
     */
    @Test
    public void testIsNotExpired() throws IOException{
        int id = 2;
        String username = "user2";
        long startTime = java.lang.System.currentTimeMillis() + 9999999;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isExpired(testSession);
        assertFalse(res);
    }

    /**
     * Tests that you can check if a session exists
     */
    @Test
    public void testSessionExists() throws IOException{
        int id = 2;
        String username = "user2";
        long startTime = 0;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isExpired(testSession);
        assertTrue(res);
    }

    /**
     * Tests that you cant check if a session exists if its username and id dont exist
     */
    @Test
    public void testSessionExistsNull() throws IOException{
        Integer id = 99;
        String username = null;
        long startTime = 0;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.sessionExists(testSession);
        assertFalse(res);
    }

    /**
     * Tests that you can check if a session exists if its username doesnt exist
     */
    @Test
    public void testSessionExistsNullUser() throws IOException{
        Integer id = 2;
        String username = null;
        long startTime = 0;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.sessionExists(testSession);
        assertTrue(res);
    }

    /**
     * Tests that you can check if a session exists if its id doesnt exist
     */
    @Test
    public void testSessionExistsNullId() throws IOException{
        Integer id = 99;
        String username = "admin";
        long startTime = 0;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.sessionExists(testSession);
        assertTrue(res);
    }

    /**
     * Tests that you can check if an admin is authorized
     */
    @Test
    public void testIsAuthorizedAdmin() throws IOException{
        int id = 3;
        String username = "admin";
        long startTime = 123456;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, "admin", true);
        assertTrue(res);
    }

    /**
     * Tests that you can check if an admin is authorized
     */
    @Test
    public void testIsAuthorizedAdminID() throws IOException{
        int id = 3;
        String username = "admin";
        long startTime = 123456;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, id, true);
        assertTrue(res);
    }

    /**
     * Tests that you can check if an admin is not authorized if their session is expired
     */
    @Test
    public void testIsAuthorizedAdminExpired() throws IOException{
        mockObjectMapper = mock(ObjectMapper.class);
        testSessions = new Session[1];
        testSessions[0] = new Session(3, "admin", 0);

        when(mockObjectMapper
            .readValue(new File(filename),Session[].class))
                .thenReturn(testSessions);

        this.sessionFileDAO = new SessionFileDAO(filename, mockObjectMapper);

        int id = 3;
        String username = "admin";
        long startTime = 123456;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, "admin", true);
        assertFalse(res);
    }

    /**
     * Tests that you can check if an admin is not authorized if their session is expired
     */
    @Test
    public void testIsAuthorizedAdminExpiredID() throws IOException{
        mockObjectMapper = mock(ObjectMapper.class);
        testSessions = new Session[1];
        testSessions[0] = new Session(3, "admin", 0);

        when(mockObjectMapper
            .readValue(new File(filename),Session[].class))
                .thenReturn(testSessions);

        this.sessionFileDAO = new SessionFileDAO(filename, mockObjectMapper);

        int id = 3;
        String username = "admin";
        long startTime = 123456;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, id, true);
        assertFalse(res);
    }

    /**
     * Tests that you can check if an admin is not authorized if their session is expired
     */
    @Test
    public void testIsAuthorizedNoAdmins() throws IOException{
        mockObjectMapper = mock(ObjectMapper.class);
        testSessions = new Session[0];

        when(mockObjectMapper
            .readValue(new File(filename),Session[].class))
                .thenReturn(testSessions);

        this.sessionFileDAO = new SessionFileDAO(filename, mockObjectMapper);

        int id = 3;
        String username = "admin";
        long startTime = 123456;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, username, true);
        assertFalse(res);
    }

    /**
     * Tests that you can check if an admin is not authorized if their session is expired
     */
    @Test
    public void testIsAuthorizedNoAdminsID() throws IOException{
        mockObjectMapper = mock(ObjectMapper.class);
        testSessions = new Session[0];

        when(mockObjectMapper
            .readValue(new File(filename),Session[].class))
                .thenReturn(testSessions);

        this.sessionFileDAO = new SessionFileDAO(filename, mockObjectMapper);

        int id = 3;
        String username = "admin";
        long startTime = 123456;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, id, true);
        assertFalse(res);
    }

    /**
     * Tests that you can check if a user is authorized
     */
    @Test
    public void testIsAuthorized() throws IOException{
        int id = 2;
        String username = "user2";
        long startTime = java.lang.System.currentTimeMillis() + 9999999;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, username, false);
        assertTrue(res);
    }

    /**
     * Tests that you can check if a user is authorized
     */
    @Test
    public void testIsAuthorizedID() throws IOException{
        int id = 2;
        String username = "user2";
        long startTime = java.lang.System.currentTimeMillis() + 9999999;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, id, false);
        assertTrue(res);
    }

    /**
     * Tests that you can check if a user is authorized if the session is expired
     */
    @Test
    public void testIsAuthorizedExpired() throws IOException{
        int id = 2;
        String username = "user2";
        long startTime = 0;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, username, false);
        assertFalse(res);
    }

    /**
     * Tests that you can check if a user is authorized if the session is expired
     */
    @Test
    public void testIsAuthorizedExpiredID() throws IOException{
        int id = 2;
        String username = "user2";
        long startTime = 0;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, id, false);
        assertFalse(res);
    }

    /**
     * Tests that you can check if a user is authorized if the session is null
     */
    @Test
    public void testIsAuthorizedNull() throws IOException{
        Session testSession = null;

        boolean res = sessionFileDAO.isAuthorized(testSession, "username", false);
        assertFalse(res);
    }

    /**
     * Tests that you can check if a user is authorized if the session is null
     */
    @Test
    public void testIsAuthorizedNullID() throws IOException{
        Session testSession = null;

        boolean res = sessionFileDAO.isAuthorized(testSession, 1, false);
        assertFalse(res);
    }

    /**
     * Tests that you can check if a user is authorized if they are not the correct user
     */
    @Test
    public void testIsAuthorizedWrongUser() throws IOException{
        int id = 2;
        String username = "user2";
        long startTime = java.lang.System.currentTimeMillis() + 9999999;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, "user1", false);
        assertFalse(res);
    }

    /**
     * Tests that you can check if a user is authorized if they are not the correct user
     */
    @Test
    public void testIsAuthorizedWrongID() throws IOException{
        int id = 2;
        String username = "user2";
        long startTime = java.lang.System.currentTimeMillis() + 9999999;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, 3, false);
        assertFalse(res);
    }

    /**
     * Tests that you can check if a user is authorized if the username is null
     */
    @Test
    public void testIsAuthorizedNullName() throws IOException{
        int id = 2;
        String username = null;
        long startTime = java.lang.System.currentTimeMillis() + 9999999;
        Session testSession = new Session(id, username, startTime);

        boolean res = sessionFileDAO.isAuthorized(testSession, username, false);
        assertFalse(res);
    }
}