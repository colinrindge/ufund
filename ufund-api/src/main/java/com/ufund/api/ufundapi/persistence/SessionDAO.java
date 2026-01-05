package com.ufund.api.ufundapi.persistence;

import java.io.IOException;

import com.ufund.api.ufundapi.model.Session;

/**
 * Defines the interface for Session object persistance
 */
public interface SessionDAO {

    /**
     * Creates a session with the username and id, replacing any sessions with a matching id
     * @param Username
     * @return the created Session, or null if non-unique name
     * @throws IOException
     */
    Session createSession(int id, String Username) throws IOException;

    /**
     * Gets a session by its id
     * @param id
     * @return a Session
     * @throws IOException
     */
    Session getSession(int id) throws IOException;

    /**
     * Gets a session by the username associated with it
     * @param Username
     * @return a Session
     * @throws IOException
     */
    Session getSessionByUser(String Username) throws IOException;

    /**
     * Replaces an already-existing session
     * @param session
     * @return the updated session
     * @throws IOException
     */
    Session updateSession(Session session) throws IOException;

    /**
     * Deletes a session by id
     * @param id
     * @return the deleted session, or null if no session matched
     * @throws IOException
     */
    Session deleteSession(int id) throws IOException;

    /**
     * Checks if a session has expired or not
     * @param session
     * @return true if expired, false otherwise
     * @throws IOException
     */
    Boolean isExpired(Session session) throws IOException;

    /**
     * Checks if a session with a matching username/id exists
     * @param session
     * @return true if a match is found, false otherwise
     * @throws IOException
     */
    Boolean sessionExists(Session session) throws IOException;

    /**
     * Checks if the given session is authorized
     *  A session is authorized if:
     *      An admin is logged in
     *      The session matches the given username
     * @param session
     * @param username
     * @param admin if admins should be auto-authorized
     * @return
     * @throws IOException
     */
    Boolean isAuthorized(Session session, String username, Boolean admin) throws IOException;

    /**
     * Checks if the given session is authorized by id
     *  A session is authorized if:
     *      An admin is logged in
     *      The session matches the given id
     * @param session
     * @param username
     * @param admin if admins should be auto-authorized
     * @return
     * @throws IOException
     */
    Boolean isAuthorized(Session session, int id, Boolean admin) throws IOException;

}
