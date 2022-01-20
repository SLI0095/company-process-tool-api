package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.repositories.StateRepository;
import com.semestral_project.company_process_tool.services.StateService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StateController {

    @Autowired
    StateService stateService;

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
    public ResponseEntity<ResponseMessage> updateState(@PathVariable Long id, @RequestBody State state) {
        int ret = stateService.updateState(id, state);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("State id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("State id: " + id + " does not exist"));
        }
    }
}
