package com.ufund.api.ufundapi.persistence;

import java.io.IOException;
import java.util.List;

import com.ufund.api.ufundapi.model.Need;

/**
 * Defines the interface for Need object persistence
 * 
 * @author Colin Rindge
 */
public interface CupboardDAO {

    /**
     * Creates and saves a {@linkplain Need need}
     * 
     * @param need {@linkplain Need need} object to be created and saved
     * @return new {@link Need need} if successful, false otherwise 
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need createNeed(Need need) throws IOException;

    /**
     * Checks if a need with the passed name already exists
     * 
     * @param need {@linkplain Need need} object to check
     * @return true if there is already a need with the same name, false otherwise
     */
    boolean needExists(Need need);

    /**
     * Checks if a need with the specified ID exists
     * 
     * @param id The ID of the need to check
     * @return true if a need with the given ID exists, false otherwise
     */
    boolean needExistsById(int id);

    /**
     * Retrieves all needs from the cupboard
     * 
     * @return array of all {@link Need needs}, may be empty
     */
    Need[] getAllNeeds();

    /**
     * Searches for a need by name containing the specified text
     * 
     * @param containsText The text to search for in need names
     * @return the first {@link Need need} that contains the text in its name, or null if not found
     */
    List<Need> getNeed(String containsText);

    /**
     * Updates an existing need with the specified ID
     * 
     * @param id The ID of the need to update
     * @param updatedNeed The updated need object
     * @return the updated {@link Need need} if successful, null if not found
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need updateNeed(int id, Need updatedNeed) throws IOException;

    /**
     * Deletes a need with the specified ID
     * 
     * @param id The ID of the need to delete
     * @return true if the need was deleted successfully, false if not found
     * 
     * @throws IOException if an issue with underlying storage
     */
    boolean deleteNeed(int id) throws IOException;

    /**
     * Generates an array of {@linkplain Need needs} from the map for any
     * {@linkplain Need needs} that contains the text specified by containsText
     * <br>
     * If containsText is null, the array contains all of the {@linkplain Need needs}
     * in the map
     * 
     * @return  The array of {@link Need needs}, may be empty
     */
    Need[] getNeedsArray(String containsText);

    /**
    * Gets a need based on its ID
     */
    Need getNeed(int id);
}