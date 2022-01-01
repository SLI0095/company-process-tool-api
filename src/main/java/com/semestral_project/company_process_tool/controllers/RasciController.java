package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.RasciOld;
import com.semestral_project.company_process_tool.repositories.RasciRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class RasciController {

    private final RasciRepository rasciRepository;

    public RasciController(RasciRepository rasciRepository) {
        this.rasciRepository = rasciRepository;
    }

    @JsonView(Views.RasciGeneral.class)
    @GetMapping("/rasci")
    public ResponseEntity<List<RasciOld>> getRasci() {
        try {
            return ResponseEntity.ok((List<RasciOld>) rasciRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }

    }

    @PostMapping("/rasci")
    public ResponseEntity<ResponseMessage> addRasci(@RequestBody RasciOld rasci) {
        try {
            rasciRepository.save(rasci);
            return ResponseEntity.ok(new ResponseMessage("Rasci added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/rasci/{id}")
    public ResponseEntity<ResponseMessage> removeDocument(@PathVariable Long id) {
        try {
            rasciRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Rasci id: " + id + " is deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @JsonView(Views.RasciGeneral.class)
    @GetMapping("/rasci/{id}")
    public ResponseEntity<RasciOld> rasciById(@PathVariable Long id) {
        Optional<RasciOld> rasciData = rasciRepository.findById(id);
        if(rasciData.isPresent()){
            return ResponseEntity.ok(rasciData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PutMapping("/rasci/{id}")
    public ResponseEntity<ResponseMessage> updateRasci(@PathVariable Long id, @RequestBody RasciOld rasci) {
        Optional<RasciOld> rasciData = rasciRepository.findById(id);
        if(rasciData.isPresent()){
            RasciOld rasci_ = rasciData.get();
            rasci_.setRole_type(rasci.getRole_type());
            rasci_.setUser(rasci.getUser());
            rasci_.setActivity(rasci.getActivity());

            rasciRepository.save(rasci_);
            return ResponseEntity.ok(new ResponseMessage("Rasci id: " + id + " is updated"));
        }
        return ResponseEntity.badRequest().body(new ResponseMessage("Rasci id: " + id + " does not exist"));
    }
}
