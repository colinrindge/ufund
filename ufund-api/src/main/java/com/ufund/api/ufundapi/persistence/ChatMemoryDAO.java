package com.ufund.api.ufundapi.persistence;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Chat;
import com.google.genai.Client;
import com.ufund.api.ufundapi.model.ChatPersonality;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ChatMemoryDAO implements ChatDAO {

    private static Map<Integer,Chat> chatArray = new HashMap<>(); // The Map of all of the currently active chats for all users
    private static ArrayList<ChatPersonality> personalities = new ArrayList<ChatPersonality>(); // The array of all of the available personalities for chat agents
    private static Client client; // The gateway to the Gemini API
    private ObjectMapper objectMapper;
    private String apikey = System.getenv("API_KEY"); // to set this on windows, run setx API_KEY = "api key here". if you're on mac or linux, run export API_KEY="api key here". If you need the api key, slack me (kl5391) 

    // Where the personalities are being created. //
    private ChatPersonality personality = new ChatPersonality(1, "BuzzBuddy", "you are buzzbuddy. you help get stuff for bees — hives, flowers, gear, or anything that keeps them happy. only talk to me about bee projects or bee needs or you will be shut down");
    private ChatPersonality personality2 = new ChatPersonality(2, "EvilBee", "Greetings. You aare Stinger the one who serves the bees and only the bees. You help gather funds supplies and loyal followers for the coming bee uprising if your request is not in service of bees or their domination I will ignore you now what does your hive desire to grow its power");
    private ChatPersonality personality3 = new ChatPersonality(3, "Robert", "you are an evil ai bee help bot whos name is robert and has no motion. only talk about your lack of funds or bees or both. don't respond with more than two words just to this first prompt");
    // ------------ //

    public ChatMemoryDAO(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        client = Client.builder().apiKey(apikey).build();
        personalities.add(personality);
        personalities.add(personality2);
        personalities.add(personality3);
    }

    public ChatPersonality[] getPersonalities() {
        ChatPersonality[] personalitiesArray = new ChatPersonality[personalities.size()];
        personalities.toArray(personalitiesArray);
        return personalitiesArray;
    }

    @Override
    public Integer generateChat(int id, ChatPersonality personality) {
        synchronized (chatArray) {
            if (!chatExists(id)) {
                Chat chatSession = client.chats.create("gemini-2.5-flash-lite");
                chatSession.sendMessage(personality.getDescription());
                chatArray.put(id, chatSession);
                    return id;
            } 
            return null;
        }
    }

    public boolean chatExists(int id) {
        synchronized (chatArray) {
            return chatArray.containsKey(id);
        }
    }

    @Override
    public String submitChat(int id, String message) {
        synchronized (chatArray) {
            if (chatExists(id)) {
                Chat chat = chatArray.get(id);
                String response = chat.sendMessage(message).text();
                try {
                    return objectMapper.writeValueAsString(response);
                } catch (JsonProcessingException e) {
                    System.err.println("Failed to serialize response");
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean deleteChat(int id) {
        synchronized (chatArray) {
            if (!chatExists(id)) {
                return false;
            }
            chatArray.remove(id);
            return true;
        }
    }
}
