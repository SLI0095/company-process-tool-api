package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("process")
public class Process extends Element{

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
    private List<Element> elements = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "bpmn_id")
    private BPMNfile workflow;

    @OneToMany(mappedBy ="process", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<HistoryBPMN> historyWorkflow = new ArrayList<>();

    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE)
    private List<ProcessMetric> metrics = new ArrayList<>();

    @ManyToMany(mappedBy = "canBeUsedIn", cascade = CascadeType.DETACH)
    private List<Element> usableElements = new ArrayList<>();

    public Process() {
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

    public BPMNfile getWorkflow() {
        return workflow;
    }

    public void setWorkflow(BPMNfile workflow) {
        this.workflow = workflow;
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

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public List<HistoryBPMN> getHistoryWorkflow() {
        return historyWorkflow;
    }

    public void setHistoryWorkflow(List<HistoryBPMN> historyWorkflow) {
        this.historyWorkflow = historyWorkflow;
    }

    public List<ProcessMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<ProcessMetric> metrics) {
        this.metrics = metrics;
    }
}
