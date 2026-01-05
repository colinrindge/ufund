    package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Represents a Need
 * 
 * @author Colin Rindge
 */
public class Need {

    @JsonProperty("id") private int id;
    @JsonProperty("name") private String name;
    @JsonProperty("cost") private int cost;
    @JsonProperty("quantity") private int quantity;
    @JsonProperty("type") private String type;
    @JsonProperty("description") private String description;

    /**
     * Create a Need with the given name, cost, quantity, and type
     * @param name The name of the need
     * @param cost The cost of the need
     * @param quantity The quantity of the need
     * @param type The type of the need
     * @param description A description of the need
     */
    public Need(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("cost") int cost, @JsonProperty("quantity") int quantity, @JsonProperty("type") String type, @JsonProperty("description") String description) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.quantity = quantity;
        this.type = type;
        this.description = description;
    }

    /**
     * Retrieves the id of the need
     * @return The id of the need
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the name of the need
     * @return The name of the need
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the cost of the need
     * @return The cost of the need
     */
    public int getCost() {
        return cost;
    }

    /**
     * Retrieves the quantity of the need
     * @return The quantity of the need
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves the type of the need
     * @return The type of the need
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the description of the need
     * @return The description of the need
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the id of the need
     * @param id The id of the need
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the name of the need
     * @param name The name of the need
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the cost of the need
     * @param cost The cost of the need
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Sets the quantity of the need
     * @param quantity The quantity of the need
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the type of the need
     * @param type The type of the need
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the description of the need
     * @param description The description of the need
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Need [name=%s, cost=%d, quantity=%d, type=%s, description=%s]", name, cost, quantity, type, description);
    }
}