package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("task")
public class Task extends Element{

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<TaskStep> steps = new ArrayList<>();
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;

    @JsonView(Views.Basic.class)
    private String taskType = "task";

    @JsonView(Views.Basic.class)
    @ManyToMany(mappedBy = "asMandatoryInput")
    private List<WorkItem> mandatoryInputs = new ArrayList<>();

//    @ManyToMany(mappedBy = "asOptionalInput")
//    private List<WorkItem> optionalInputs = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @ManyToMany(mappedBy = "asOutput")
    private List<WorkItem> outputs = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<Rasci> rasciList = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @ManyToMany(mappedBy = "asGuidanceWorkItem")
    private List<WorkItem> guidanceWorkItems = new ArrayList<>();


    @ManyToMany(mappedBy = "canBeUsedIn", cascade = CascadeType.DETACH)
    private List<Role> usableRoles = new ArrayList<>();

    @ManyToMany(mappedBy = "canBeUsedIn", cascade = CascadeType.DETACH)
    private List<WorkItem> usableWorkItems = new ArrayList<>();



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

    public List<Rasci> getRasciList() {
        return rasciList;
    }

    public void setRasciList(List<Rasci> rasciList) {
        this.rasciList = rasciList;
    }

    public List<WorkItem> getGuidanceWorkItems() {
        return guidanceWorkItems;
    }

    public void setGuidanceWorkItems(List<WorkItem> guidanceWorkItems) {
        this.guidanceWorkItems = guidanceWorkItems;
    }


    public List<WorkItem> getMandatoryInputs() {
        return mandatoryInputs;
    }

    public void setMandatoryInputs(List<WorkItem> mandatoryInputs) {
        this.mandatoryInputs = mandatoryInputs;
    }

//    public List<WorkItem> getOptionalInputs() {
//        return optionalInputs;
//    }
//
//    public void setOptionalInputs(List<WorkItem> optionalInputs) {
//        this.optionalInputs = optionalInputs;
//    }

    public List<WorkItem> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<WorkItem> outputs) {
        this.outputs = outputs;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}
