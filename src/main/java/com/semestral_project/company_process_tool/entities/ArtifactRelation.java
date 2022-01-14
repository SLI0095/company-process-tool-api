package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "artifact_relations")
public class ArtifactRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Artifact baseArtifact;

    @JsonIgnore
    @ManyToOne
    private Artifact relatedArtifact;

    private String relationType;

    public ArtifactRelation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Artifact getBaseArtifact() {
        return baseArtifact;
    }

    public void setBaseArtifact(Artifact baseArtifact) {
        this.baseArtifact = baseArtifact;
    }

    public Artifact getRelatedArtifact() {
        return relatedArtifact;
    }

    public void setRelatedArtifact(Artifact relatedArtifact) {
        this.relatedArtifact = relatedArtifact;
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
