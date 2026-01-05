package com.ufund.api.ufundapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.SessionDAO;
import com.ufund.api.ufundapi.model.Need;

/**
 * Handles the REST API requests for the Need resource
 * 
 * @author Colin Rindge, Kyle Long
 */
@RestController
@RequestMapping("cupboard")
public class CupboardController {
    private CupboardDAO cupboardDAO;
    private SessionDAO sessionDAO;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param cupboardDao The {@link CupboardDAO Cupboard Data Access Object} to perform CRUD operations
     */
    public CupboardController(CupboardDAO cupboardDAO, SessionDAO sessionDAO) {
        this.cupboardDAO = cupboardDAO;
        this.sessionDAO = sessionDAO;
    }

    /**
     * Creates a {@linkplain Need need} with the provided need object
     * 
     * @param need - The {@link Need need} to create
     * 
     * @return ResponseEntity with created {@link Need need} object and HTTP status of CREATED<br>
     * ResponseEntity with HTTP status of CONFLICT if {@link Need need} object already exists<br>
     * ResponseEntity with HTTP status of UNAUTHORIZED if an admin is not signed in
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("")
    public ResponseEntity<Need> createNeed(@RequestBody Need need) {

        try {
            if (sessionDAO.isAuthorized(null, null, true)){
                if(cupboardDAO.needExists(need)){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                Need newNeed = cupboardDAO.createNeed(need);
                return new ResponseEntity<Need>(newNeed, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        catch(IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing {@linkplain Need need} with the provided ID
     * 
     * @param id The ID of the need to update
     * @param updatedNeed The updated need object
     * 
     * @return ResponseEntity with updated {@link Need need} object and HTTP status of OK<br>
     * ResponseEntity with HTTP status of NOT_FOUND if need doesn't exist<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PutMapping("/{id}")
    public ResponseEntity<Need> updateNeed(@PathVariable int id, @RequestBody Need updatedNeed) {
        try {
            if (sessionDAO.isAuthorized(null, null, true)){
                if(!cupboardDAO.needExistsById(id)) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
                }
                Need need = cupboardDAO.updateNeed(id, updatedNeed);
                return new ResponseEntity<Need>(need, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
                
        } catch(IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@linkplain Need need} with the specified ID
     * 
     * @param id The ID of the need to delete
     * 
     * @return ResponseEntity with HTTP status of OK if deleted successfully<br>
     * ResponseEntity with HTTP status of NOT_FOUND if need doesn't exist<br>
     * ResponseEntity with HTTP status of UNAUTHORIZED if admin isn't signed in
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteNeed(@PathVariable int id) {
        try {
            if (sessionDAO.isAuthorized(null, null, true)){
                if(!cupboardDAO.deleteNeed(id)) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Searches for a {@linkplain Need need} by name containing the specified text
     * 
     * @param containsText The text to search for in need names
     * 
     * @return ResponseEntity with found {@link Need need} object and HTTP status of OK<br>
     * ResponseEntity with HTTP status of NOT_FOUND if no need matches<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Need[]> searchNeed(@PathVariable("name") String containsText) {
        Need[] needs = cupboardDAO.getNeedsArray(containsText);
        if (needs.length != 0) {
            return new ResponseEntity<>(needs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(needs, HttpStatus.NOT_FOUND);
        }
    }
 
    /**
     * Retrieves all {@linkplain Need needs} from the cupboard
     * 
     * @return ResponseEntity with array of {@link Need need} objects and HTTP status of OK<br>
     * ResponseEntity with HTTP status of OK with empty list if there are no needs
     */
    @GetMapping()
    public ResponseEntity<Need[]> getAllNeeds() {
        Need[] needs = cupboardDAO.getAllNeeds();
        return new ResponseEntity<>(needs, HttpStatus.OK);
    }

    /**
     * Responds to the GET request for a {@linkplain Need need} for the given id
     * 
     * @param id The id used to locate the {@link Need need}
     * 
     * @return ResponseEntity with {@link Need need} object and HTTP status of OK if found<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Need> getNeed(@PathVariable int id) {
        Need need = cupboardDAO.getNeed(id);
        if (need != null)
            return new ResponseEntity<Need>(need,HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
}
