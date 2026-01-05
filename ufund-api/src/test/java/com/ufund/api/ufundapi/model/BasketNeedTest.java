package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class BasketNeedTest {
    /**
     * Tests if a basket need can be successfully created
     */
    @Test
    public void testCreateBasketNeed(){
        Need need = new Need(0, null, 0, 0, null, null);
        int count = 1;

        BasketNeed basketNeed = new BasketNeed(need, count);

        assertEquals(need, basketNeed.getNeed());
        assertEquals(count, basketNeed.getCount());
    }

    /**
     * Tests if a need's parameters can be successfully set
     */
    @Test
    public void testEditCount(){
        Need need = new Need(0, null, 0, 0, null, null);
        int count = 1;
        int count2 = 2;

        BasketNeed basketNeed = new BasketNeed(need, count);
        basketNeed.editCount(count2);

        assertEquals(count2, basketNeed.getCount());
    }
    
    /**
     * Tests if a need's parameters can be successfully set
     */
    @Test
    public void testEditCount2(){
        Need need = new Need(0, null, 0, 0, null, null);
        int count = 0;
        int count2 = 2;

        BasketNeed basketNeed = new BasketNeed(need, count);
        basketNeed.editCount(count2);

        assertEquals(count2, basketNeed.getCount());
    }
}
