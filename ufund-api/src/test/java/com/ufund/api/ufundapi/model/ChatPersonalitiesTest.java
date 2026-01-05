package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-Tier")
class ChatPersonalitiesTest {
    
    @Test
    void testCreateChatPersonality() {
        ChatPersonality personality = new ChatPersonality(1, "Chat One", "Chat Personality One");
        assertInstanceOf(ChatPersonality.class, personality);
    }

    @Test
    void testGetChatID() {
        int id = 1;
        ChatPersonality personality = new ChatPersonality(id, "Chat One", "Chat Personality One");
        assertEquals(id, personality.getId());

    }
    
    @Test
    void testGetChatName() {
        String name = "Test personality";
        ChatPersonality personality = new ChatPersonality(1, name, "Chat Personality One");
        assertEquals(name, personality.getName());
    }

    @Test
    void testGetChatDescription() {
        String description = "Chat Personality One";
        ChatPersonality personality = new ChatPersonality(1, "Chat One", description);
        assertEquals(description, personality.getDescription());
    }

}
