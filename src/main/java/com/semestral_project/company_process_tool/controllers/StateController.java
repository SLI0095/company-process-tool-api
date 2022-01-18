package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.Activity;
import com.semestral_project.company_process_tool.entities.Artifact;
import com.semestral_project.company_process_tool.entities.State;
import com.semestral_project.company_process_tool.entities.Task;
import com.semestral_project.company_process_tool.repositories.StateRepository;
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
    StateRepository stateRepository;

    @GetMapping("/states")
    public ResponseEntity<List<State>> getStates() {
        try {
            return org.springframework.http.ResponseEntity.ok((List<State>) stateRepository.findAll());
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }

    @PostMapping("/states")
    public ResponseEntity<ResponseMessage> addState(@RequestBody State state) {
        try {
            stateRepository.save(state);
            return ResponseEntity.ok(new ResponseMessage("State added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("State could not be added"));
        }
    }

    @GetMapping("/states/{id}")
    public ResponseEntity<State> getStates(@PathVariable Long id) {
        Optional<State> stateData = stateRepository.findById(id);

        if(stateData.isPresent()) {
            return ResponseEntity.ok(stateData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PutMapping("/states/{id}")
    public ResponseEntity<ResponseMessage> updateState(@PathVariable Long id, @RequestBody State state) {
        Optional<State> stateData = stateRepository.findById(id);

        if(stateData.isPresent()){
            State state_ = stateData.get();
            state_.setStateName(state.getStateName());
            state_.setStateDescription(state.getStateDescription());

            stateRepository.save(state_);
            return ResponseEntity.ok(new ResponseMessage("State id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("State id: " + id + " does not exist"));
        }
    }
}
