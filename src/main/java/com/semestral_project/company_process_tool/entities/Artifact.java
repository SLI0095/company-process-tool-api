package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("artifact")
public class Artifact extends WorkItem {

    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;
    @Column(columnDefinition="LONGTEXT")
    private String briefOutline;
    @Column(columnDefinition="LONGTEXT")
    private String notation;
    @Column(columnDefinition="LONGTEXT")
    private String impactOfNotHaving;
    @Column(columnDefinition="LONGTEXT")
    private String reasonForNotNeeding;

    @ManyToOne
    private State artifactState;

    @JsonIgnore
    @OneToMany(mappedBy ="baseArtifact", cascade = CascadeType.REMOVE)
    private List<ArtifactRelation> asBase;

//    @OneToOne
//    @JoinColumn(name = "artifact_relations_id", referencedColumnName = "id")
//    private ArtifactRelation relationToAnotherArtifact;

    @OneToMany(mappedBy ="relatedArtifact")
    private  List<ArtifactRelation> relationsToAnotherArtifacts;

    public Artifact() {
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getKeyConsiderations() {
        return keyConsiderations;
    }

    public void setKeyConsiderations(String keyConsiderations) {
        this.keyConsiderations = keyConsiderations;
    }

    public String getBriefOutline() {
        return briefOutline;
    }

    public void setBriefOutline(String briefOutline) {
        this.briefOutline = briefOutline;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getImpactOfNotHaving() {
        return impactOfNotHaving;
    }

    public void setImpactOfNotHaving(String impactOfNotHaving) {
        this.impactOfNotHaving = impactOfNotHaving;
    }

    public String getReasonForNotNeeding() {
        return reasonForNotNeeding;
    }

    public void setReasonForNotNeeding(String reasonForNotNeeding) {
        this.reasonForNotNeeding = reasonForNotNeeding;
    }

    public State getArtifactState() {
        return artifactState;
    }

    public void setArtifactState(State artifactState) {
        this.artifactState = artifactState;
    }

    public List<ArtifactRelation> getAsBase() {
        return asBase;
    }

    public void setAsBase(List<ArtifactRelation> asBase) {
        this.asBase = asBase;
    }

    public List<ArtifactRelation> getRelationsToAnotherArtifacts() {
        return relationsToAnotherArtifacts;
    }

    public void setRelationsToAnotherArtifacts(List<ArtifactRelation> relationsToAnotherArtifacts) {
        this.relationsToAnotherArtifacts = relationsToAnotherArtifacts;
    }
}
