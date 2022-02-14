package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("document")
public class Document extends WorkItem{

    private String documentType = "";
    private String urlAddress;

//    @OneToOne
//    @JoinColumn(name = "document_relations_id", referencedColumnName = "id")
//    private DocumentRelation relationToAnotherDocument;

    @OneToMany(mappedBy ="relatedDocument")
    private  List<DocumentRelation> relationsToAnotherDocuments;

    @JsonIgnore
    @OneToMany(mappedBy ="baseDocument", cascade = CascadeType.REMOVE)
    private List<DocumentRelation> asBase;

    public Document() {
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

//    public DocumentRelation getRelationToAnotherDocument() {
//        return relationToAnotherDocument;
//    }
//
//    public void setRelationToAnotherDocument(DocumentRelation relationToAnotherDocument) {
//        this.relationToAnotherDocument = relationToAnotherDocument;
//    }


    public List<DocumentRelation> getRelationsToAnotherDocuments() {
        return relationsToAnotherDocuments;
    }

    public void setRelationsToAnotherDocuments(List<DocumentRelation> relationToAnotherDocuments) {
        this.relationsToAnotherDocuments = relationToAnotherDocuments;
    }

}
