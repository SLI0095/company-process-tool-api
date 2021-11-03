package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.InputOutput;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.InputOutputRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class InputOutputController {

    private final InputOutputRepository inputOutputRepository;

    public InputOutputController(InputOutputRepository inputOutputRepository) {
        this.inputOutputRepository = inputOutputRepository;
    }

    @JsonView(Views.Minimal.class)
    @GetMapping("/inputsoutputs")
    public ResponseEntity<List<InputOutput>> getInputsOutputs() {
        try {
            return ResponseEntity.ok((List<InputOutput>) inputOutputRepository.findAll());
        }
       catch (Exception e)
        {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }

    @PostMapping("/inputsoutputs")
    public ResponseEntity<ResponseMessage> addInputOutput(@RequestBody InputOutput inputOutput) {
        try {
            inputOutputRepository.save(inputOutput);
            return ResponseEntity.ok(new ResponseMessage("InputOutput added"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }

    }

    @DeleteMapping("/inputsoutputs/{id}")
    public ResponseEntity<ResponseMessage> removeDocument(@PathVariable Long id) {
        try {
            inputOutputRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("InputOutput id: " + id + " is deleted"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @JsonView(Views.Minimal.class)
    @GetMapping("/inputsoutputs/{id}")
    public ResponseEntity<InputOutput> inputOutputById(@PathVariable Long id) {
        Optional<InputOutput> inputOutputData = inputOutputRepository.findById(id);
        if(inputOutputData.isPresent()){
            return ResponseEntity.ok(inputOutputData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @JsonView(Views.InputActivites.class)
    @GetMapping("/inputsoutputs/{id}/Input")
    //zobrazuje aktivity kde je jako input
    public ResponseEntity<InputOutput> inputOutputByIdInput(@PathVariable Long id) {
        Optional<InputOutput> inputOutputData = inputOutputRepository.findById(id);
        if(inputOutputData.isPresent()){
            return ResponseEntity.ok(inputOutputData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @JsonView(Views.OutputActivites.class)
    @GetMapping("/inputsoutputs/{id}/Output")
    //zobrazuje aktivity kde je jako output
    public ResponseEntity<InputOutput> inputOutputByIdOutput(@PathVariable Long id) {
        Optional<InputOutput> inputOutputData = inputOutputRepository.findById(id);
        if(inputOutputData.isPresent()){
            return ResponseEntity.ok(inputOutputData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PutMapping("/inputsoutputs/{id}")
    public ResponseEntity<ResponseMessage> updateInputOutput(@PathVariable Long id, @RequestBody InputOutput inputOutput) {
        Optional<InputOutput> inputOutputData = inputOutputRepository.findById(id);
        if(inputOutputData.isPresent()){
            InputOutput inputOutput_ = inputOutputData.get();
            inputOutput_.setName(inputOutput.getName());
            inputOutput_.setState(inputOutput.getState());

            inputOutputRepository.save(inputOutput_);
            return ResponseEntity.ok(new ResponseMessage("InputOutput id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("InputOutput id: " + id + " does not exist"));
        }
    }
}
