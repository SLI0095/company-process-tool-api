package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.Document;
import com.semestral_project.company_process_tool.entities.DocumentOld;
import com.semestral_project.company_process_tool.entities.DocumentRelation;
import com.semestral_project.company_process_tool.repositories.DocumentRelationRepository;
import com.semestral_project.company_process_tool.repositories.DocumentRepository;
import com.semestral_project.company_process_tool.services.DocumentService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class DocumentController {

    @Autowired
    DocumentService documentService;
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    DocumentRelationRepository documentRelationRepository;

    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        if(documents != null){
            return ResponseEntity.ok(documents);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> documentById(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        if(document != null){
            return ResponseEntity.ok(document);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/documents")
    public ResponseEntity<ResponseMessage> addDocument(@RequestBody Document document){
        boolean ret = documentService.addDocument(document);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Document added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Document could not be added."));
        }
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<ResponseMessage> removeDocument(@PathVariable Long id) {
        boolean ret = documentService.deleteDocumentById(id);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Document id: " + id + " is deleted"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Document could not be deleted."));
        }
    }

    @PutMapping("/documents/{id}")
    public ResponseEntity<ResponseMessage> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        int ret = documentService.updateDocument(id, document);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Document id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + id + " does not exist"));
        }
    }


    @PutMapping("/documents/{id}/addRelation")
    public ResponseEntity<ResponseMessage> addRelation(@PathVariable Long id, @RequestBody Document document, @RequestParam String relationType) {
        int status = documentService.addRelationToDocument(id, document, relationType);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Document id: " + id + " is updated"));
        } else if(status == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + id + " does not exist"));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + id + " already has relation to document " + document.getId()));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Can not add relation to itself"));
        }
    }


    @PutMapping("/documents/{id}/removeRelation")
    public ResponseEntity<ResponseMessage> removeRelation(@PathVariable Long id, @RequestBody DocumentRelation documentRelation) {

        int status = documentService.removeRelationFromDocument(id, documentRelation);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Document id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + id + " does not exist"));
        }
    }
}
