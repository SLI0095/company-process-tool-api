package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.Document;
import com.semestral_project.company_process_tool.entities.DocumentRelation;
import com.semestral_project.company_process_tool.repositories.DocumentRelationRepository;
import com.semestral_project.company_process_tool.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    DocumentRelationRepository documentRelationRepository;
    @Autowired
    BPMNparser bpmNparser;

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

    public List<Document> getAllDocuments(){
        try {
            return (List<Document>) documentRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Document getDocumentById(long id){
        Optional<Document> documentData = documentRepository.findById(id);

        if(documentData.isPresent()) {
            return documentData.get();
        }
        else return null;
    }

    public boolean addDocument(Document document){
        try {
            documentRepository.save(document);
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteDocumentById(long id){
        try {
            if(bpmNparser.removeWorkItemFromAllWorkflows(documentRepository.findById(id).get()))
            {
                documentRepository.deleteById(id);
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int updateDocument(long id, Document document){
        Optional<Document> documentData = documentRepository.findById(id);
        if(documentData.isPresent()){
            Document document_ = documentData.get();
            String oldName = document_.getName();
            document_ = fillDocument(document_, document);
            documentRepository.save(document_);
            bpmNparser.updateWorkItemInAllWorkflows(document_,true, null);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    //@Transactional
    public int addRelationToDocument(long id, Document document, String relationType){
        if(id == document.getId())
            return 4;

        Optional<Document> documentData = documentRepository.findById(id);

        if(documentData.isPresent()){
            Document document_ = documentData.get();

            List<DocumentRelation> relations = document_.getRelationsToAnotherDocuments();
            for(DocumentRelation relation : relations){
                if(relation.getBaseDocument().getId() == document.getId()){
                    return 3;
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
            return 1;
        }
        else
        {
            return 2;
        }
    }

    //@Transactional
    public int removeRelationFromDocument(long id, DocumentRelation documentRelation){
        Optional<Document> documentData = documentRepository.findById(id);

        if(documentData.isPresent()){
            Document document_ = documentData.get();
            DocumentRelation documentRelation_ = documentRelationRepository.findById(documentRelation.getId()).get();

            List<DocumentRelation> relations = document_.getRelationsToAnotherDocuments();
            relations.remove(documentRelation_);
            document_.setRelationsToAnotherDocuments(relations);
            documentRepository.save(document_);
            documentRelationRepository.delete(documentRelation_);
            return 1;
        }
        else
        {
            return 2;
        }
    }
}
