package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.State;
import com.semestral_project.company_process_tool.repositories.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
