package com.ufund.api.ufundapi.persistence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;

@Tag("Persistence-Tier")
public class CupboardFileTest {

    @TempDir
    Path tempDir;

    private CupboardFileDAO cupboard;
    private ObjectMapper fakeMapper;

    @BeforeEach
    public void setup() throws IOException {
        Path testFile = tempDir.resolve("cupboard_test.json");
        Files.writeString(testFile, "[]"); 

        fakeMapper = new ObjectMapper();
        cupboard = new CupboardFileDAO(testFile.toString(), fakeMapper);
    }


    /*
     * Test that adding a need to the cupboard is working.
     * Should be true because createNeed only returns true upon creation success
     */
    @Test void test_create_need() throws IOException {
        Need need1 = new Need(1, "Test need 1", 100, 1, "Test type", "I am a test need");

        assertTrue(cupboard.createNeed(need1) instanceof Need);
    }

    /*
     * Test that the needs array can be resolved and that the length of the result is equal to the number of created needs
     */
    @Test
    public void test_get_all_needs() throws IOException {
        Need need1 = new Need(1, "Test need 1", 100, 1, "Test type", "I am a test need");
        Need need2 = new Need(2, "Test need 2", 100, 1, "Test type", "I am a test need");

        cupboard.createNeed(need1);
        cupboard.createNeed(need2);

        Need[] result = cupboard.getAllNeeds();

        assertEquals(2, result.length);

    }

    /*
     * Tests that the need added to the cupboard has the same name as the one gotten using the method that gets a 
     * need based upon its ID
     */
    @Test
    public void test_get_need_by_id() throws IOException {
        Need need1 = new Need(1, "Test need 1", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        assertEquals(need1.getName(),cupboard.getNeed(1).getName());
    }

    /*
     * Test that if a Need ID that does not exist is requested, the API returns null
     */
    @Test
    public void test_get_need_by_id_dne() throws IOException {
        Need need1 = new Need(1, "Test need 1", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        assertEquals(null, cupboard.getNeed(2));
    }


    /*
     * Tests get need by name by searching the name of a need in the cupboard
     */
    @Test
    public void test_get_need_by_name() throws IOException {
        Need need1 = new Need(1, "Search for me", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        Need searchedNeed = cupboard.getNeed("Search").get(0);
        assertTrue(searchedNeed instanceof Need);
    }

    /*
     * Tests get need by name by searching the name of a need that does not exist in the cupboard
     */
    @Test
    public void test_get_need_by_name_dne() throws IOException {
        Need need1 = new Need(1, "Search for me", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        Need searchedNeed = cupboard.getNeed("I CANT FIND YOU").get(0);
        assertEquals(null, searchedNeed);
    }

    /*
     * Search for a need based on the need object when it exists in the cupboard
     */
    @Test
    public void test_need_exists() throws IOException {
        Need need1 = new Need(1, "Test Need 1", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        assertTrue(cupboard.needExists(need1));
    }

    /*
     * Search for a need based on the need ID when it does exist in the cupboard
     */
    @Test
    public void test_get_need_id_does_exist() throws IOException {
        Need need1 = new Need(1, "Test Need 1", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        assertTrue(cupboard.getNeed(1) instanceof Need);
    }

    /*
     * Search for a need based on the need ID when it does not exist in the cupboard
     */
    @Test
    public void test_get_need_id_does_not_exist() throws IOException {
        Need need1 = new Need(1, "Test Need 1", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        assertEquals(cupboard.getNeed(2), null);;
    }

    /*
     * Test that a need can be deleted by ensuring it returns true when the needs id is passed
     */
    @Test
    public void test_delete_need() throws IOException {
        Need need1 = new Need(1, "Test Need 1", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        assertTrue(cupboard.deleteNeed(need1.getId()));
    }

     /*
     * Test that a need that does not exist can' be deleted by ensuring it returns false when the needs id is passed
     */
    @Test
    public void test_delete_need_dne() throws IOException {
        Need need1 = new Need(999, "Test Need 2", 100, 1, "Test type", "I am a test need");
        assertFalse(cupboard.deleteNeed(need1.getId()));
    }

     /*
     * Test that a need that a need exists based on its ID
     */
    @Test
    public void test_need_exists_by_id() throws IOException {
        Need need1 = new Need(1, "Test Need 1", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        assertTrue(cupboard.needExistsById(need1.getId()));
    }
     
    /*
     * Test that a need that does not exist based on its ID
     */
    @Test
    public void test_need_exists_by_id_dne() throws IOException {
        Need need1 = new Need(1, "Test Need 1", 100, 1, "Test type", "I am a test need");
        Need need2 = new Need(1, "Search for me", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        assertTrue(cupboard.needExistsById(need2.getId()));
    }

    /*
     * Test a need can be updated by creating a need and passing in a new one containing the "update"
     */
    @Test
    public void test_update_need() throws IOException {
        Need need1 = new Need(1, "Test Need 1", 100, 1, "Test type", "I am a test need");
        Need updatededneed = new Need(1, "Test Need 2", 100, 1, "Test type", "I am a test need");
        cupboard.createNeed(need1);
        assertTrue(cupboard.updateNeed(need1.getId(), updatededneed) instanceof Need);
    }

    /*
     * Test a need can be updated by creating a need and passing in a new one containing the "update"
     */
    @Test
    public void test_need_exists_dne() throws IOException {
        Need need1 = new Need(999, "Test Need 1", 100, 1, "Test type", "I am a test need");
        assertFalse(cupboard.needExists(need1));
    }
}