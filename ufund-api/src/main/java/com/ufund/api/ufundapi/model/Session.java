package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Session class used in authentication
 */
public class Session {
    @JsonProperty("id") private int id;
    @JsonProperty("userName") private String userName;
    @JsonProperty("timer") private long timer;


    /**
     * Creates Session with given id, username, and timer
     * @param id the session id
     * @param userName the linked userName
     * @param timer the moment, in milliseconds, the session was last validated
     */
    public Session(@JsonProperty("id") int id, @JsonProperty("userName") String userName, @JsonProperty("timer") long timer){
        this.id = id;
        this.userName = userName;
        this.timer = timer;
    }

     /**
     * gets the session ID
     * @return the session ID
     */
    public int getId(){
        return id;
    }

     /**
     * gets session's Username
     * @return the userName
     */
    public String getUserName(){
        return userName;
    }

     /**
     * gets session's timer
     * @return the timer
     */
    public long getTimeStart(){
        return timer;
    }

    @Override
    public String toString(){
        return "Session ID: "+id+", Username: "+userName+", creation time: "+timer;
    }


    
}