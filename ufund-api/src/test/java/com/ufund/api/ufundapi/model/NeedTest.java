package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class NeedTest {
    /**
     * Tests if a need can be successfully created
     */
    @Test
    public void testCreateNeed(){
        int id = 99;
        String name = "Test need";
        int cost = 100;
        int quantity = 10;
        String type = "Example type";
        String description = "A need created for testing purposes";

        Need need = new Need(id, name, cost, quantity, type, description);

        assertEquals(id, need.getId());
        assertEquals(name, need.getName());
        assertEquals(cost, need.getCost());
        assertEquals(quantity, need.getQuantity());
        assertEquals(type, need.getType());
        assertEquals(description, need.getDescription());
    }

    /**
     * Tests if a need's parameters can be successfully set
     */
    @Test
    public void testSetters(){
        int id = 99;
        String name = "Test need";
        int cost = 100;
        int quantity = 10;
        String type = "Example type";
        String description = "A need created for testing purposes";

        int id2 = 1;
        String name2 = "Revised Need";
        int cost2 = 20;
        int quantity2 = 3;
        String type2 = "Edited type";
        String description2 = "A need that has been edited using setters";

        Need need = new Need(id, name, cost, quantity, type, description);

        need.setId(id2);
        need.setName(name2);
        need.setCost(cost2);
        need.setQuantity(quantity2);
        need.setType(type2);
        need.setDescription(description2);

        assertEquals(id2, need.getId());
        assertEquals(name2, need.getName());
        assertEquals(cost2, need.getCost());
        assertEquals(quantity2, need.getQuantity());
        assertEquals(type2, need.getType());
        assertEquals(description2, need.getDescription());

    }

    /**
     * Tests a need's toString function
     */
    @Test
    public void testToString(){
        int id = 99;
        String name = "Test need";
        int cost = 100;
        int quantity = 10;
        String type = "Example type";
        String description = "A need created for testing purposes";

        String expectedOutput = "Need [name=Test need, cost=100, quantity=10, type=Example type, description=A need created for testing purposes]";

        Need need = new Need(id, name, cost, quantity, type, description);

        assertEquals(expectedOutput, need.toString());
    }
}