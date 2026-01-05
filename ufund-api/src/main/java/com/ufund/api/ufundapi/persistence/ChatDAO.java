package com.ufund.api.ufundapi.persistence;
import com.ufund.api.ufundapi.model.ChatPersonality;

/**
 * Defines the interface for Chat object persistence
 * 
 * @author Kyle Long
 */

public interface ChatDAO {
    String submitChat(int id, String message);
    boolean deleteChat(int id);
    Integer generateChat(int id, ChatPersonality personality);
    boolean chatExists(int id);
    ChatPersonality[] getPersonalities();
}