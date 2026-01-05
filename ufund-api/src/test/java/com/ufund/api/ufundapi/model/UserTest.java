package com.ufund.api.ufundapi.model;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class UserTest {
    @Test
    public void testConstructor(){
        // public User(@JsonProperty("id") int id, @JsonProperty("Username")String Username){
        int ExpectedId = 99;
        String ExpectedUsername = "John Doe";
        String ExpectedPassword = "abc";
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(ExpectedId, ExpectedUsername,ExpectedPassword,security);

        assertEquals(ExpectedUsername, user.getUserName());
        assertEquals(ExpectedId, user.getId());
    }

    @Test
    public void testConstructorAdmin(){
        // public User(@JsonProperty("id") int id, @JsonProperty("Username")String Username){
        int ExpectedId = 99;
        String ExpectedUsername = "admin";
        String ExpectedPassword = "abcd";
        ArrayList<String> security = new ArrayList<String>();
        User user = new User(ExpectedId, ExpectedUsername,ExpectedPassword,security);

        assertEquals(ExpectedUsername, user.getUserName());
        assertEquals(ExpectedId, user.getId());
    }

    @Test
    public void testBasket(){
        //User starts with a empty basket
        int ExpectedId = 10;
        String ExpectedUsername = "John Deer";
        String ExpectedPassword = "abcd";
        ArrayList<String> security = new ArrayList<String>();
        User JohnDeer = new User(ExpectedId, ExpectedUsername,ExpectedPassword,security);
        assertEquals((JohnDeer.getBasket()).size(),0);

        //User can add a need to their basket and the size will increase
        Need MoneyNeed = new Need(10,"Money",100,1,"Money","Money for Isreal");
        JohnDeer.addNeed(MoneyNeed);
        assertEquals((JohnDeer.getBasket()).size(),1);

        Need MoneyNeed2 = new Need(110,"Money2",100,1,"Money2","Money for Isreal 2");
        JohnDeer.removeNeed(MoneyNeed2);
        assertEquals((JohnDeer.getBasket()).size(),1);

        //Users can remove needs from their baskets and the size will decrease
        JohnDeer.removeNeed(MoneyNeed);
        assertEquals((JohnDeer.getBasket()).size(),0);


    }

    @Test
    public void getBasketNeedTest(){
        int ExpectedId = 10;
        String ExpectedUsername = "John Deer";
        String ExpectedPassword = "abcd";
        ArrayList<String> security = new ArrayList<String>();

        User JohnDeer = new User(ExpectedId, ExpectedUsername,ExpectedPassword,security);
        Need MoneyNeed = new Need(10,"Money",100,1,"Money","Money for Isreal");
        Need MoneyNeed2 = new Need(110,"Money2",100,1,"Money2","Money for Isreal 2");

        BasketNeed ret = JohnDeer.getBasketNeed(MoneyNeed);
        assertEquals(null, ret);

        JohnDeer.addNeed(MoneyNeed);
        BasketNeed ret2 = JohnDeer.getBasketNeed(MoneyNeed2);
        assertEquals(null, ret2);

    }


    @Test
    public void testToString(){
        String Username = "John Pear";
        int id = 15;
        String ExpectedPassword = "njmk";
        ArrayList<String> security = new ArrayList<String>();
        User JohnPear = new User(id, Username,ExpectedPassword,security);
        String toString = JohnPear.toString();
        assertEquals("Username: John Pear UserId: 15", toString);
    }

    @Test
    public void testCheckSecurity(){
        String Username = "John Pear";
        int id = 15;
        String ExpectedPassword = "njmk";
        ArrayList<String> security = new ArrayList<String>();
        security.add("answer");
        User JohnPear = new User(id, Username,ExpectedPassword,security);
        boolean result = JohnPear.checkSecurity(0, "answer");
        assertTrue(result);
    }

    @Test
    public void testCheckSecurityFalse(){
        String Username = "John Pear";
        int id = 15;
        String ExpectedPassword = "njmk";
        ArrayList<String> security = new ArrayList<String>();
        security.add("answer");
        User JohnPear = new User(id, Username,ExpectedPassword,security);
        boolean result = JohnPear.checkSecurity(0, "notanswer");
        assertFalse(result);
    }

    @Test
    public void testCheckSecurityEmpty(){
        String Username = "John Pear";
        int id = 15;
        String ExpectedPassword = "njmk";
        ArrayList<String> security = new ArrayList<String>();
        User JohnPear = new User(id, Username,ExpectedPassword,security);
        boolean result = JohnPear.checkSecurity(0, "notanswer");
        assertFalse(result);
    }

    @Test
    public void testSetSecurity(){
        String Username = "John Pear";
        int id = 15;
        String ExpectedPassword = "njmk";
        ArrayList<String> security = new ArrayList<String>();
        security.add("oldanswer");
        User JohnPear = new User(id, Username,ExpectedPassword,security);
        ArrayList<String> result = JohnPear.setSecurity(0, "newanswer");
        assertEquals(result.get(0), "newanswer");
    }

    @Test
    public void testSetSecurityOutOfBounds(){
        String Username = "John Pear";
        int id = 15;
        String ExpectedPassword = "njmk";
        ArrayList<String> security = new ArrayList<String>();
        security.add("a");
        User JohnPear = new User(id, Username,ExpectedPassword,security);
        ArrayList<String> result = JohnPear.setSecurity(12, "answer");
        assertEquals(result, security);
    }

    @Test
    public void testSetSecurityEmpty(){
        String Username = "John Pear";
        int id = 15;
        String ExpectedPassword = "njmk";
        ArrayList<String> security = new ArrayList<String>();
        User JohnPear = new User(id, Username,ExpectedPassword,security);
        ArrayList<String> result = JohnPear.setSecurity(0, "answer");
        assertEquals(result, new ArrayList<String>());
    }

    @Test
    public void testHashMatches(){
        String Username = "John Pear";
        int id = 15;
        String ExpectedPassword = "hashed password";
        ArrayList<String> security = new ArrayList<String>();
        User JohnPear = new User(id, Username,ExpectedPassword,security);
        boolean result = JohnPear.hashMatches("hashed password");
        assertTrue(result);
    }

    @Test
    public void testHashMatchesFalse(){
        String Username = "John Pear";
        int id = 15;
        String ExpectedPassword = "hashed password";
        ArrayList<String> security = new ArrayList<String>();
        User JohnPear = new User(id, Username,ExpectedPassword,security);
        boolean result = JohnPear.hashMatches("wrong hashed password");
        assertFalse(result);
    }

}
