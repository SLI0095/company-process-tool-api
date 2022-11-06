package com.semestral_project.company_process_tool.entities.snapshots;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SnapshotProcess extends SnapshotElement {

    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @Column(columnDefinition="LONGTEXT")
    private String scope;
    @Column(columnDefinition="LONGTEXT")
    private String usageNotes;
    @Column(columnDefinition="LONGTEXT")
    private String alternatives;
    @Column(columnDefinition="LONGTEXT")
    private String howToStaff;
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;

    @ManyToMany(mappedBy = "partOfProcess", cascade = CascadeType.DETACH)
    private List<SnapshotElement> elements = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "snapshot_bpmn_id")
    private SnapshotBPMN workflow;

    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE)
    private List<SnapshotProcessMetric> metrics = new ArrayList<>();

    public SnapshotProcess() {
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUsageNotes() {
        return usageNotes;
    }

    public void setUsageNotes(String usageNotes) {
        this.usageNotes = usageNotes;
    }

    public String getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(String alternatives) {
        this.alternatives = alternatives;
    }

    public String getHowToStaff() {
        return howToStaff;
    }

    public void setHowToStaff(String howToStaff) {
        this.howToStaff = howToStaff;
    }

    public String getKeyConsiderations() {
        return keyConsiderations;
    }

    public void setKeyConsiderations(String keyConsiderations) {
        this.keyConsiderations = keyConsiderations;
    }

    public List<SnapshotElement> getElements() {
        return elements;
    }

    public void setElements(List<SnapshotElement> elements) {
        this.elements = elements;
    }

    public SnapshotBPMN getWorkflow() {
        return workflow;
    }

    public void setWorkflow(SnapshotBPMN workflow) {
        this.workflow = workflow;
    }

    public List<SnapshotProcessMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<SnapshotProcessMetric> metrics) {
        this.metrics = metrics;
    }
}
