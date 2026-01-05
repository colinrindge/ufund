package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Represents a Need in a basket with a count of how many are in the basket
 * 
 * @author Timothy Steffens
 */
public class BasketNeed {
    @JsonProperty("need") private Need need;
    @JsonProperty("count") private int count;

    /**
     * Creates a basket need
     * @param need the need
     * @param count the count of the need
     */
    public BasketNeed(@JsonProperty("need") Need need, @JsonProperty("count") int count){
        this.need = need;
        this.count = count;
    }

    public Need getNeed(){
        return this.need;
    }

    public int getCount(){
        return this.count;
    }

    public boolean editCount(int num){
        if (this.count + num < 0) {
            return false;
        }
        this.count = num;
        return true;
    }
}
