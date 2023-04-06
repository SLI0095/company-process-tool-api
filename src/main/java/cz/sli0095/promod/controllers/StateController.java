package cz.sli0095.promod.controllers;

import cz.sli0095.promod.entities.State;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.services.StateService;
import cz.sli0095.promod.utils.ResponseMessage;
import cz.sli0095.promod.utils.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StateController {

    @Autowired
    StateService stateService;

    @JsonView(Views.Default.class)
    @GetMapping("/states")
    public ResponseEntity<List<State>> getStates() {
        List<State> states = stateService.getAllStates();
        if(states != null){
            return ResponseEntity.ok(states);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/states")
    public ResponseEntity<ResponseMessage> addState(@RequestBody State state) {
        boolean ret = stateService.addState(state);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("State added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("State could not be added"));
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/states/{id}")
    public ResponseEntity<State> getStateById(@PathVariable Long id) {
        State state = stateService.getStateById(id);
        if(state != null){
            return ResponseEntity.ok(state);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/states/{id}")
    public ResponseEntity<ResponseMessage> updateState(@PathVariable Long id, @RequestBody State state, @RequestParam long userId) {
        int ret = stateService.updateState(id, state, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("State id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("State id: " + id + " does not exist"));
        }
    }
}
