package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "document_relations")
public class DocumentRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Document baseDocument;

    @JsonIgnore
    @ManyToOne
    private Document relatedDocument;

    private String relationType;

    public DocumentRelation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Document getBaseDocument() {
        return baseDocument;
    }

    public void setBaseDocument(Document baseDocument) {
        this.baseDocument = baseDocument;
    }

    public Document getRelatedDocument() {
        return relatedDocument;
    }

    public void setRelatedDocument(Document relatedDocument) {
        this.relatedDocument = relatedDocument;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    //    private boolean contributes;
//    //extends
//    private boolean extending;
//    private boolean replaces;
//    private boolean extendsAndReplace;
}
