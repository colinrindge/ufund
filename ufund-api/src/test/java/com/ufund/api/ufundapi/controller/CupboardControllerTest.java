package com.ufund.api.ufundapi.controller;

import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.SessionDAO;
import com.ufund.api.ufundapi.model.Need;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Test the Cupboard Controller class
 * 
 * @author Colin Rindge
 */
@Tag("Controller-tier")
public class CupboardControllerTest {
    private CupboardController cupboardController;
    private CupboardDAO mockCupboardDAO;
    private SessionDAO mockSessionDAO;

    /**
     * Before each test, create a new cupboardController object and inject
     * a mock Cupboard DAO
     */
    @BeforeEach
    public void setupCupboardController() {
        mockCupboardDAO = mock(CupboardDAO.class);
        mockSessionDAO = mock(SessionDAO.class);
        cupboardController = new CupboardController(mockCupboardDAO, mockSessionDAO);
    }
    
    /**
     * Tests if a need can be successfully created
     */
    @Test
    public void testCreateNeed() throws IOException {
        Need need = new Need(99, "Test Need", 100, 12, "Example Type", "This is a test need");

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);
        when(mockCupboardDAO.needExists(need)).thenReturn(false);
        when(mockCupboardDAO.createNeed(need)).thenReturn(need);

        ResponseEntity<Need> response = cupboardController.createNeed(need);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    /**
     * Tests that a need can not be created if current user is unauthorized
     */
    @Test
    public void testCreateNeedUnauthorized() throws IOException {
        Need need = new Need(99, "Test Need", 100, 12, "Example Type", "This is a test need");

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(false);

        ResponseEntity<Need> response = cupboardController.createNeed(need);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * Tests that a need will not be created if it already exists
     */
    @Test
    public void testCreateNeedAlreadyExists() throws IOException {
        Need need = new Need(99, "Test Need", 100, 12, "Example Type", "This is a test need");

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);
        when(mockCupboardDAO.needExists(need)).thenReturn(true);

        ResponseEntity<Need> response = cupboardController.createNeed(need);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * Tests that a need will not be created if an IO Exception is thrown
     */
    @Test
    public void testCreateNeedException() throws IOException {
        Need need = new Need(99, "Test Need", 100, 12, "Example Type", "This is a test need");

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);
        when(mockCupboardDAO.needExistsById(99)).thenReturn(true);
        doThrow(new IOException()).when(mockCupboardDAO).createNeed(need);

        ResponseEntity<Need> response = cupboardController.createNeed(need);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests that an existing need can be updated
     */
    @Test
    public void testUpdateNeed() throws IOException {
        Need need = new Need(99, "Test Need", 100, 12, "Example Type", "This is a test need");
        int id = 99;

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);
        when(mockCupboardDAO.needExistsById(id)).thenReturn(true);
        when(mockCupboardDAO.updateNeed(id, need)).thenReturn(need);

        ResponseEntity<Need> response = cupboardController.updateNeed(id, need);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    /**
     * Tests that a need can not be updated if unauthorized
     */
    @Test
    public void testUpdateNeedUnauthorized() throws IOException {
        Need need = new Need(99, "Test Need", 100, 12, "Example Type", "This is a test need");
        int id = 99;

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(false);

        ResponseEntity<Need> response = cupboardController.updateNeed(id, need);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * Tests that a need that doesn't exist can't be updated
     */
    @Test
    public void testUpdateNeedDoesntExist() throws IOException {
        Need need = new Need(99, "Test Need", 100, 12, "Example Type", "This is a test need");
        int id = 99;

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);
        when(mockCupboardDAO.needExistsById(id)).thenReturn(false);

        ResponseEntity<Need> response = cupboardController.updateNeed(id, need);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests that a need will not be updated if an IO Exception is thrown
     */
    @Test
    public void testUpdateNeedException() throws IOException {
        Need need = new Need(99, "Test Need", 100, 12, "Example Type", "This is a test need");
        int id = 99;

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);
        when(mockCupboardDAO.needExistsById(id)).thenReturn(true);
        doThrow(new IOException()).when(mockCupboardDAO).updateNeed(id, need);

        ResponseEntity<Need> response = cupboardController.updateNeed(id, need);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests that a need can be deleted
     */
    @Test
    public void testDeleteNeed() throws IOException {
        int id = 99;

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);
        when(mockCupboardDAO.deleteNeed(id)).thenReturn(true);

        ResponseEntity<Boolean> response = cupboardController.deleteNeed(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Tests that a need can't be deleted if unauthorized
     */
    @Test
    public void testDeleteNeedUnauthorized() throws IOException {
        int id = 99;

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(false);

        ResponseEntity<Boolean> response = cupboardController.deleteNeed(id);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * Tests that a need can't be deleted if it doesn't exist
     */
    @Test
    public void testDeleteNeedDoesntExist() throws IOException {
        int id = 99;

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);
        when(mockCupboardDAO.deleteNeed(id)).thenReturn(false);

        ResponseEntity<Boolean> response = cupboardController.deleteNeed(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests that a need can't be deleted when an exception is thrown
     */
    @Test
    public void testDeleteNeedException() throws IOException {
        int id = 99;

        when(mockSessionDAO.isAuthorized(null, null, true)).thenReturn(true);
        doThrow(new IOException()).when(mockCupboardDAO).deleteNeed(id);

        ResponseEntity<Boolean> response = cupboardController.deleteNeed(id);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests that needs can be searched for
     */
    @Test
    public void testSearchNeed() throws IOException {
        Need need1 = new Need(99, "Test Need 1", 100, 12, "Example Type 1", "This is a test need 1");
        Need need2 = new Need(99, "Test Need 2", 200, 22, "Example Type 2", "This is a test need 2");
        Need[] result = {need1, need2};
        String search = "test";

        when(mockCupboardDAO.getNeedsArray(search)).thenReturn(result);

        ResponseEntity<Need[]> response = cupboardController.searchNeed(search);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    /**
     * Tests that no needs will be returned if search results in nothing
     */
    @Test
    public void testSearchNeedNoResult() throws IOException {
        Need[] result = {};
        String search = "test";

        when(mockCupboardDAO.getNeedsArray(search)).thenReturn(result);

        ResponseEntity<Need[]> response = cupboardController.searchNeed(search);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    /**
     * Tests that the entire cupboard can be gotten
     */
    @Test
    public void testGetAllNeeds() throws IOException {
        Need need1 = new Need(99, "Test Need 1", 100, 12, "Example Type 1", "This is a test need 1");
        Need need2 = new Need(99, "Test Need 2", 200, 22, "Example Type 2", "This is a test need 2");
        Need[] result = {need1, need2};

        when(mockCupboardDAO.getAllNeeds()).thenReturn(result);

        ResponseEntity<Need[]> response = cupboardController.getAllNeeds();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    /**
     * Tests that the entire cupboard can be gotten, even when empty
     */
    @Test
    public void testGetAllNeedsEmpty() throws IOException {
        Need[] result = {};

        when(mockCupboardDAO.getAllNeeds()).thenReturn(result);

        ResponseEntity<Need[]> response = cupboardController.getAllNeeds();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    /**
     * Tests that a need can be gotten
     */
    @Test
    public void testGetNeed() throws IOException {
        Need need = new Need(99, "Test Need", 100, 12, "Example Type", "This is a test need");
        int id = 99;

        when(mockCupboardDAO.getNeed(id)).thenReturn(need);

        ResponseEntity<Need> response = cupboardController.getNeed(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    /**
     * Tests that a need that doesn't exist can't be gotten
     */
    @Test
    public void testGetNeedDoesntExist() throws IOException {
        Need need = null;
        int id = 99;

        when(mockCupboardDAO.getNeed(id)).thenReturn(need);

        ResponseEntity<Need> response = cupboardController.getNeed(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    

}
