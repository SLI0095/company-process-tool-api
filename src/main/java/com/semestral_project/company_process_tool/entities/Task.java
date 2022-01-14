package com.semestral_project.company_process_tool.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("task")
public class Task extends Element{

    @OneToMany(mappedBy = "task")
    private List<TaskStep> steps;
    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;

    @ManyToMany(mappedBy = "asPrimaryPerformer")
    private List<Role> primaryPerformers;
    @ManyToMany(mappedBy = "asAdditionalPerformer")
    private List<Role> additionalPerformers;

    @ManyToMany(mappedBy = "asMandatoryInput")
    private List<WorkItem> mandatoryInputs;

    @ManyToMany(mappedBy = "asOptionalInput")
    private List<WorkItem> optionalInputs;

    @ManyToMany(mappedBy = "asOutput")
    private List<WorkItem> outputs;

    public Task() {
    }

    public List<TaskStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TaskStep> steps) {
        this.steps = steps;
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

    public List<Role> getPrimaryPerformers() {
        return primaryPerformers;
    }

    public void setPrimaryPerformers(List<Role> primaryPerformers) {
        this.primaryPerformers = primaryPerformers;
    }

    public List<Role> getAdditionalPerformers() {
        return additionalPerformers;
    }

    public void setAdditionalPerformers(List<Role> additionalPerformers) {
        this.additionalPerformers = additionalPerformers;
    }

    public List<WorkItem> getMandatoryInputs() {
        return mandatoryInputs;
    }

    public void setMandatoryInputs(List<WorkItem> mandatoryInputs) {
        this.mandatoryInputs = mandatoryInputs;
    }

    public List<WorkItem> getOptionalInputs() {
        return optionalInputs;
    }

    public void setOptionalInputs(List<WorkItem> optionalInputs) {
        this.optionalInputs = optionalInputs;
    }

    public List<WorkItem> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<WorkItem> outputs) {
        this.outputs = outputs;
    }
}
