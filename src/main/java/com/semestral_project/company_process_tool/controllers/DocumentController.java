package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.Document;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.DocumentRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class DocumentController {

    private final DocumentRepository documentRepository;

    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @JsonView(Views.Minimal.class)
    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getDocuments(){
        try {
            return ResponseEntity.ok((List<Document>) documentRepository.findAll());
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }

    }

    @PostMapping("/documents")
    public ResponseEntity<ResponseMessage> addDocument(@RequestBody Document document){
        try {
            documentRepository.save(document);
            return ResponseEntity.ok(new ResponseMessage("Document added"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<ResponseMessage> removeDocument(@PathVariable Long id) {
        try {
            documentRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Document id: " + id + " is deleted"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/documents/{id}")
    public ResponseEntity<ResponseMessage> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        Optional<Document> documentData = documentRepository.findById(id);

        if(documentData.isPresent()){
            Document document_ = documentData.get();
            document_.setName(document.getName());
            document_.setSource(document.getSource());

            documentRepository.save(document_);
            return ResponseEntity.ok(new ResponseMessage("Document id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + id + " does not exist"));
        }
    }

    @JsonView(Views.Minimal.class)
    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> documentById(@PathVariable Long id) {
        Optional<Document> documentData = documentRepository.findById(id);

        if(documentData.isPresent()) {
            return ResponseEntity.ok(documentData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }
}
