package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-Tier")
public class SessionTest {

    //Initialize test data
    int id = 1;
    String username = "test_user";
    long timer = 100;

    //Create test Session object
    Session sessionTest = new Session(id, username, timer);

    /*
     * Tests the getID for the test session against the initial data
     */
    @Test
    public void test_get_id() {
        assertEquals(sessionTest.getId(), id);
    }

    /*
     * Tests the getUserName for the test session against the initial data
     */
    @Test
    public void test_get_user() {
        assertEquals(sessionTest.getUserName(), username);
    }

    /*
     * Tests the getTimeStart for the test session against the initial data
     */
    @Test
    public void test_get_timer() {
        assertEquals(sessionTest.getTimeStart(), timer);
    }

    /*
     * Tests the toString for the test session against the initial data
     */
    @Test
    public void test_to_string() {
        String expectedString = "Session ID: " + id + ", Username: " + username + ", creation time: " + timer;
        assertEquals(expectedString, sessionTest.toString());
    }
}
