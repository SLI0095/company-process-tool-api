package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.Document;
import com.semestral_project.company_process_tool.entities.DocumentOld;
import com.semestral_project.company_process_tool.entities.DocumentRelation;
import com.semestral_project.company_process_tool.repositories.DocumentRelationRepository;
import com.semestral_project.company_process_tool.repositories.DocumentRepository;
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
    DocumentRepository documentRepository;
    @Autowired
    DocumentRelationRepository documentRelationRepository;

    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getDocuments() {
        try {
            return ResponseEntity.ok((List<Document>) documentRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> documentById(@PathVariable Long id) {
        Optional<Document> documentData = documentRepository.findById(id);

        if(documentData.isPresent()) {
            return ResponseEntity.ok(documentData.get());
        }
        else return ResponseEntity.badRequest().body(null);
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
            document_ = fillDocument(document_, document);

            documentRepository.save(document_);
            return ResponseEntity.ok(new ResponseMessage("Document id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + id + " does not exist"));
        }
    }


    @Transactional
    @PutMapping("/documents/{id}/addRelation")
    public ResponseEntity<ResponseMessage> addRelation(@PathVariable Long id, @RequestBody Document document, @RequestParam String relationType) {
        if(id == document.getId())
            return ResponseEntity.badRequest().body(new ResponseMessage("Can not add relation to itself"));

        Optional<Document> documentData = documentRepository.findById(id);

        if(documentData.isPresent()){
            Document document_ = documentData.get();

            List<DocumentRelation> relations = document_.getRelationsToAnotherDocuments();
            for(DocumentRelation relation : relations){
                if(relation.getBaseDocument().getId() == document.getId()){
                    return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + id + " already has relation to document " + document.getId()));
                }
            }
            DocumentRelation relation = new DocumentRelation();
            relation.setBaseDocument(document);
            relation.setRelatedDocument(document_);
            relation.setRelationType(relationType);
            relation = documentRelationRepository.save(relation);
            relations.add(relation);
            document_.setRelationsToAnotherDocuments(relations);
            documentRepository.save(document_);
            return ResponseEntity.ok(new ResponseMessage("Document id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + id + " does not exist"));
        }
    }


    @Transactional
    @PutMapping("/documents/{id}/removeRelation")
    public ResponseEntity<ResponseMessage> removeRelation(@PathVariable Long id, @RequestBody DocumentRelation documentRelation) {
        Optional<Document> documentData = documentRepository.findById(id);

        if(documentData.isPresent()){
            Document document_ = documentData.get();
            DocumentRelation documentRelation_ = documentRelationRepository.findById(documentRelation.getId()).get();

            List<DocumentRelation> relations = document_.getRelationsToAnotherDocuments();
            relations.remove(documentRelation_);
            document_.setRelationsToAnotherDocuments(relations);
            documentRepository.save(document_);
            documentRelationRepository.delete(documentRelation_);
            return ResponseEntity.ok(new ResponseMessage("Document id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + id + " does not exist"));
        }
    }


    private Document fillDocument(Document oldDocument, Document updatedDocument){
        oldDocument.setName(updatedDocument.getName());
        oldDocument.setBriefDescription(updatedDocument.getBriefDescription());
        oldDocument.setMainDescription(updatedDocument.getMainDescription());
        oldDocument.setVersion(updatedDocument.getVersion());
        oldDocument.setChangeDate(updatedDocument.getChangeDate());
        oldDocument.setChangeDescription(updatedDocument.getChangeDescription());
        oldDocument.setDocumentType(updatedDocument.getDocumentType());
        oldDocument.setUrlAddress(updatedDocument.getUrlAddress());
        return oldDocument;
    }


}
