package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.Activity;
import com.semestral_project.company_process_tool.entities.Document;
import com.semestral_project.company_process_tool.entities.InputOutput;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.DocumentRepository;
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
    private final DocumentRepository documentRepository;

    public InputOutputController(InputOutputRepository inputOutputRepository, DocumentRepository documentRepository) {
        this.inputOutputRepository = inputOutputRepository;
        this.documentRepository = documentRepository;
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
    public ResponseEntity<ResponseMessage> removeInputOutput(@PathVariable Long id) {
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

    @PutMapping("/inputsoutputs/{id}/addDocument")
    public ResponseEntity<ResponseMessage> addDocument(@PathVariable Long id, @RequestBody Document document) {
        Optional<InputOutput> inputOutputData = inputOutputRepository.findById(id);
        if(inputOutputData.isPresent()){
            InputOutput inputOutput_ = inputOutputData.get();
            List<Document> documents = inputOutput_.getDocuments();
            for(Document doc : documents)
            {
                if(doc.getId() == document.getId())
                {
                    return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + document.getId() +  " already added."));
                }
            }
            Optional<Document> documentData = documentRepository.findById(document.getId());
            if(documentData.isPresent())
            {
                Document document_ = documentData.get();
                inputOutput_.addDocument(document_);
                inputOutputRepository.save(inputOutput_);
                documentRepository.save(document_);
                return ResponseEntity.ok(new ResponseMessage("Document id: " + document.getId() +  " added"));
            }
            else
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + document.getId() + " does not exist."));
            }
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Input/output id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/inputsoutputs/{id}/removeDocument")
    public ResponseEntity<ResponseMessage> removeDocument(@PathVariable Long id, @RequestBody Document document) {
        Optional<InputOutput> inputOutputData = inputOutputRepository.findById(id);
        if (inputOutputData.isPresent()) {
            InputOutput inputOutput_ = inputOutputData.get();
            List<Document> documents = inputOutput_.getDocuments();
            for (Document doc : documents) {
                if (doc.getId() == document.getId()) {
                    Optional<Document> documentData = documentRepository.findById(document.getId());
                    if (documentData.isPresent()) {
                        Document document_ = documentData.get();
                        inputOutput_.removeDocument(document_);
                        inputOutputRepository.save(inputOutput_);
                        documentRepository.save(document_);
                        return ResponseEntity.ok(new ResponseMessage("Document id: " + document.getId() + " was removed"));
                    } else {
                        return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + document.getId() + " does not exist."));
                    }
                }
            }
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + document.getId() + " not in documents of Input/output: " + id + "."));
        }
        return ResponseEntity.badRequest().body(new ResponseMessage("Input/output id: " + id + " does not exist"));
    }
}
