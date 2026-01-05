package com.ufund.api.ufundapi.model;

import java.util.ArrayList;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufund.api.ufundapi.util.Passwordhasher;

/**
 * User class to keep track of basic info of 
 */
public class User {
    @JsonProperty("id") private int id;
    @JsonProperty("userName") private String userName;
    @JsonProperty("role") private Role role;
    @JsonProperty("basket") private ArrayList<BasketNeed> basket;
    @JsonProperty("password") private String password;
    @JsonProperty("security") private ArrayList<String> security;
    @JsonProperty("restricted") private boolean restricted;
    


    /**
     * create user with given id and Username
     * @param id the id of the user
     * @param userName the userName of the user
     * @param password password of user
     * @param security the answers to the security
     */

    @JsonCreator
    public User(@JsonProperty("id") int id, @JsonProperty("userName")String userName, @JsonProperty("password") String password, @JsonProperty("security") ArrayList<String> security){
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.basket = new ArrayList<>();
        if(userName.equals("admin")){
            this.role = Role.MANAGER;
        } else {
            this.role = Role.HELPER;
        }
        this.security = security;
        this.restricted = false;
        
    }

     /**
     * create user with given id and Username
     * @param id the id of the user
     * @param userName the userName of the user
     * @param restricted the restricted status of the user
     */
    public User(@JsonProperty("id") int id, @JsonProperty("userName")String userName, @JsonProperty("password") String password, @JsonProperty("restricted") boolean restricted, @JsonProperty("basket") ArrayList<BasketNeed> basket, @JsonProperty("security") ArrayList<String> security){
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.basket = basket;
        if(userName.equals("admin")){
            this.role = Role.MANAGER;
        } else {
            this.role = Role.HELPER;
        }
        this.security = security;
        this.restricted = restricted;
        
    }

    /**
     * gets the id of user
     * @return the id of user
     */
    public int getId(){
        return id;
    }
    public void setPassword(String password){
         this.password = Passwordhasher.hashPassword(password); 
    }

    public void setPasswordNoHash(String password){
         this.password = password; 
    }


    public boolean hashMatches(String hashed){
        if(hashed.equals(password)){
            return true;
        }
        return false;
    }

    public boolean passwordMatches(String GivenPassword){
        String hashed = Passwordhasher.hashPassword(GivenPassword);
        if(hashed.equals(password)){
            return true;
        }
        return false;
    }

    /**
     * Retrieves the basket
     * @return The array list of needs
     */
    public ArrayList<BasketNeed> getBasket() {
        return basket;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Adds a need to the needs list
     * @param need The need 
     */
    public void addNeed(Need need) {
        if (need != null) {
            basket.add(new BasketNeed(need, 1));
        } else {
            throw new IllegalArgumentException("Cannot add null Need to basket");
        }
    }
    /**
     * Removes a need to the needs list
     * @param need The need to remove 
     */
    public void removeNeed(Need need) {
        for (int i = 0; i < basket.size(); i++){
            if (basket.get(i).getNeed().getId() == need.getId()){
                basket.remove(i);
            }
        }
    }

    public BasketNeed getBasketNeed(Need need) {
        for (int i = 0; i < basket.size(); i++){
            if (basket.get(i).getNeed().getId() == need.getId()){
                return basket.get(i);
            }
        }
        return null;
    }

    public ArrayList<String> getSecurity(){
        return this.security;
    }

    public boolean checkSecurity(int question, String answer){
        for (int i = 0; i < security.size(); i++){
            if (security.get(i) == answer){
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> setSecurity(int question, String newAnswer){
        for (int i = 0; i < security.size(); i++){
            if (i == question){
                this.security.set(i, newAnswer);
            }
        }
        return this.security;
    }

     /**
     * gets the userNameof user
     * @return the userNameof user
     */
    public String getUserName(){
        return userName;
    }

    public boolean getRestricted(){
        return restricted;
    }

    @Override
    public String toString() {
        return "Username: "+userName + " UserId: "+id; 
    }


}
