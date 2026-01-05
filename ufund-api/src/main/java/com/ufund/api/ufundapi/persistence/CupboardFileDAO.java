package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufund.api.ufundapi.model.Need;

/**
 * Implements the functionality for JSON file-based peristance for the Cupboard
 * 
 * @author Colin Rindge, Kyle Long
 */
@Component
public class CupboardFileDAO implements CupboardDAO {
    private Map<Integer, Need> needs;
    private ObjectMapper objectMapper;
    private static int nextId;
    private String filename;

    /**
     * Creates a Cupboard File Data Access Object
     * 
     * @param filename Filename to read from and write to
     * @param objectMapper Provides JSON Object to/from Java Object serialization and deserialization
     * 
     * @throws IOException when file cannot be accessed or read from
     */
    public CupboardFileDAO(@Value("${cupboard.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load();
    }

    /**
     * Generates the next id for a new {@linkplain Need need}
     * 
     * @return The next id
     */
    private synchronized static int nextId() {
        int id = nextId;
        ++nextId;
        return id;
    }

    /**
     * Generates an array of {@linkplain Need needs} from the map
     * 
     * @return  The array of {@link Need needs}, may be empty
     */
    private Need[] getNeedsArray() {
        return getNeedsArray(null);
    }

    /**
    ** {@inheritDoc}
    */
    public Need[] getNeedsArray(String containsText) { // if containsText == null, no filter
        ArrayList<Need> needArrayList = new ArrayList<>();

        for (Need need : needs.values()) {
            if (containsText == null || need.getName().toLowerCase().contains(containsText.toLowerCase())) {
                needArrayList.add(need);
            }
        }

        Need[] needArray = new Need[needArrayList.size()];
        needArrayList.toArray(needArray);
        return needArray;
    }

    /**
     * Saves the {@linkplain Need needs} from the array list into the file as an array of JSON objects
     * 
     * @return true if the {@link Need needs} were written successfully
     * 
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        Need[] needArray = getNeedsArray();
        objectMapper.writeValue(new File(filename), needArray);
        return true;
    }

    /**
     * Loads {@linkplain Need needs} from the JSON file into the tree map
     * 
     * @return true if the file was read successfully
     * 
     * @throws IOException when file cannot be accessed or read from
     */
    private boolean load() throws IOException {
        needs = new TreeMap<>();
        nextId = 0;

        Need[] needArray = objectMapper.readValue(new File(filename),Need[].class);

        for (Need need : needArray) {
            needs.put(need.getId(),need);
            if (need.getId() > nextId)
                nextId = need.getId();
        }
        // Make the next id one greater than the maximum from the file
        ++nextId;
        return true;
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public Need createNeed(Need need) throws IOException {
        synchronized(needs) {
            Need newNeed = new Need(nextId(), need.getName(), need.getCost(), need.getQuantity(), need.getType(), need.getDescription());
            needs.put(newNeed.getId(), newNeed);
            save(); // may throw an IOException
            return newNeed;
        }
    }

    /**
    ** {@inheritDoc}
    */
    public Need updateNeed(int id, Need updatedNeed) throws IOException {
        synchronized(needs) {
            updatedNeed.setId(id);
            needs.put(id, updatedNeed);
            save();
            return updatedNeed;
        }
    }

    /**
    ** {@inheritDoc}
    */
    @Override
    public boolean needExistsById(int id) {
        return needs.containsKey(id); 
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public boolean deleteNeed(int id) throws IOException {
        synchronized(needs) {
            if (needExistsById(id)) {
                needs.remove(id);
                save();
                return true;
            } 
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need[] getAllNeeds() {
        return getNeedsArray();
    }

    class CupboardArrayList<T> extends ArrayList<Need> {
        @Override
        public Need get(int index) {
            if (this.size() == 0 || index-1 > this.size()) {
                return null;
            } else {
                return super.get(index);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CupboardArrayList<Need> getNeed(String containsText) {
        Need[] needs = getNeedsArray();
        CupboardArrayList<Need> matched_needs = new CupboardArrayList<Need>();
        for (Need need : needs) {
            if (need.getName().contains(containsText)) {
                matched_needs.add(need);
            }
        }
        return matched_needs;
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public boolean needExists(Need need){
        for(int oldId : needs.keySet()){
            if(oldId == need.getId()){
                return true;
            }
        }

        return false;
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public Need getNeed(int id) {
        synchronized(needs) {
            if (needs.containsKey(id))
                return needs.get(id);
            else
                return null;
        }
    }
}
