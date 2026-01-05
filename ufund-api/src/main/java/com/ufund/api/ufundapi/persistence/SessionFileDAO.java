package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Session;

@Component
public class SessionFileDAO implements SessionDAO {
    
    /** Map serving as a local cache of Session opjects */
    Map<Integer, Session> sessions; 

    /** Mapper object to convert between Session objects and JSON text */
    private ObjectMapper objectMapper;

    /** The filename used to store session objects */
    private String filename;

    // how long it takes a session to expire, currently 30 minutes
    private static long expiryTime = 30*60*1000;

    /**
     * Creates a Session file data object
     * @param filename the session.json file to access
     * @param objectMapper Mapper between JSON objects and Session Objects
     * @throws IOException when file cannot be accessed or read from
     */
    public SessionFileDAO(@Value("${sessions.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load();
    }

    /**
     * Generates an array of {@linkplain Session sessions} from the tree map
     * @return The array of {@link Session sessions}
     */
    private Session[] getSessionsArray() {
        ArrayList<Session> sessionArrayList = new ArrayList<>();

        for (Session session : sessions.values()) {
            sessionArrayList.add(session);
        }

        Session[] sessionArray = new Session[sessionArrayList.size()];
        sessionArrayList.toArray(sessionArray);
        return sessionArray;
    }

    /**
     * Saves the {@linkplain Session sessions} from the map into the file as an array of JSON objects
     * @return true is the {@link Session sessions} were written successfully
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        Session[] sessionArray = getSessionsArray();


        objectMapper.writeValue(new File(filename), sessionArray);
        return true;
    }

    /**
     * loads users from the JSON file into the map
     * @return true if the file was read successfully
     * @throws IOException when the file cannot be accessed or read from
     */
    private boolean load() throws IOException {
        sessions = new TreeMap<>();

        Session[] sessionArray = objectMapper.readValue(new File(filename), Session[].class);
        
        for (Session session : sessionArray){
            int sessionId = session.getId();
            sessions.put(sessionId, session);
        }
        return true;
    }

    @Override
    public Session createSession(int id, String Username) throws IOException { 
        synchronized (sessions) {
            if (Username != null){
                long newDate = java.lang.System.currentTimeMillis();
                Session newSession = new Session(id, Username, newDate);
                sessions.put(id, newSession);
                save();
                return newSession;
            } else {
                return null;
            }
        }
    }

    @Override
    public Session getSession(int id) throws IOException {
        synchronized (sessions) {
            return sessions.get(id);
        }
    }

    @Override
    public Session getSessionByUser(String userName) throws IOException {
        synchronized (sessions) {
            for (Session session : sessions.values()) {
                if (session != null){
                    if (session.getUserName() != null && session.getUserName().equals(userName)) {
                        return session;
                    }
                }   
            }
            return null;
        }
    }

    @Override
    public Session updateSession(Session session) throws IOException {
        synchronized (sessions) {
            int sessId = session.getId();
            if (!sessions.containsKey(sessId)) {
                return null; 
            }
            sessions.put(sessId, session);
            save();
            return session;
        }
    }

    @Override
    public Session deleteSession(int id) throws IOException {
        synchronized (sessions) {
            Session deleted = sessions.remove(id);
            save();
            return deleted;
        }
    }

    @Override
    public Boolean isExpired(Session session) throws IOException {
        long now = java.lang.System.currentTimeMillis();
        long before = session.getTimeStart();

        if ((now - before) > expiryTime){
            return true;
        }
        return false;
    }

    @Override
    public Boolean sessionExists(Session session) throws IOException {
        if ((getSession(session.getId()) != null) || (getSessionByUser(session.getUserName()) != null)){
            return true;
        } 
        return false;
    }

    @Override
    public Boolean isAuthorized(Session session, String userName, Boolean admin) throws IOException {
        if (admin){
            Session adminCheck = getSessionByUser("admin");
            Boolean isAdmin = adminCheck != null && !isExpired(adminCheck);

            if (isAdmin) {return true;}
        }
        if (session != null){
            Boolean userCheck = !isExpired(session);
            String sessionName = session.getUserName();
            Boolean isAuthorizedUser = sessionName != null && sessionName.equals(userName);
            
            if (userCheck && isAuthorizedUser){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
        
    }

    @Override
    public Boolean isAuthorized(Session session, int id, Boolean admin) throws IOException {
        if (admin){
            Session adminCheck = getSessionByUser("admin");
            Boolean isAdmin = adminCheck != null && !isExpired(adminCheck);

            if (isAdmin) {return true;}
        }
        if (session != null){
            Boolean userCheck = !isExpired(session);
            Boolean isAuthorizedUser = session.getId() == id;
            
            if (userCheck && isAuthorizedUser){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
        
    }

}
