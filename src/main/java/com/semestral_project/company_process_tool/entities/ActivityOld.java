package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.*;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class ActivityOld {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal.class)
    private long id;

    @JsonView(Views.Minimal.class)
    private String name;

    @Lob
    @JsonView(Views.Minimal.class)
    private String description;

//    @JsonBackReference(value = "previousActivity")
    @ManyToOne(optional = true)
    @JsonView(Views.ActivityGeneral.class)
    private ActivityOld previousActivity;

//    @JsonManagedReference(value = "nextActivities")
    @OneToMany(mappedBy = "previousActivity")
    @JsonView(Views.ProcessRender.class)
    private List<ActivityOld> nextActivities = new ArrayList<>();

//    @JsonBackReference(value = "process")
    @ManyToOne(optional = false)
    @JsonView(Views.ActivityGeneral.class)
    private ProcessOld process;

//    @JsonManagedReference(value = "rasciList")
    @JsonView(Views.ActivityRasci.class)
    @OneToMany(mappedBy = "activity",orphanRemoval = true)
    private List<RasciOld> rasciList = new ArrayList<>();

//    @JsonManagedReference(value = "inputs")
    @JsonView(Views.ActivityInputs.class)
    @ManyToMany
    @JoinTable(name = "activity_input",
    joinColumns = {@JoinColumn(name = "activity_id")},
    inverseJoinColumns = {@JoinColumn(name = "input_output_id")})
    private List<InputOutput> inputs = new ArrayList<>();

//    @JsonManagedReference(value = "outputs")
    @JsonView(Views.ActivityOutputs.class)
    @ManyToMany
    @JoinTable(name = "activity_output",
            joinColumns = {@JoinColumn(name = "activity_id")},
            inverseJoinColumns = {@JoinColumn(name = "input_output_id")})
    private List<InputOutput> outputs = new ArrayList<>();

//    @JsonManagedReference(value = "documents")
    @ManyToMany(mappedBy = "activities")
    @JsonView(Views.ActivityDocuments.class)
    private List<DocumentOld> documents = new ArrayList<>();

    public ActivityOld() { }

    public ActivityOld(String name, String description, ProcessOld process, ActivityOld previousActivity) {
        this.name = name;
        this.description = description;
        this.process = process;
        this.previousActivity = previousActivity;
    }

    public ActivityOld(String name, String description, ProcessOld process) {
        this.name = name;
        this.process = process;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProcessOld getProcess() {
        return process;
    }

    public void setProcess(ProcessOld process) {
        this.process = process;
    }

    public void setNextActivities(List<ActivityOld> nextActivities) {
        this.nextActivities = nextActivities;
    }

    public List<ActivityOld> getNextActivities() {
        return nextActivities;
    }

    public void addNextActivity(ActivityOld activity)
    {
        this.nextActivities.add(activity);
        activity.setPreviousActivity(activity);
    }

    public void setPreviousActivity(ActivityOld previousActivity) {
        this.previousActivity = previousActivity;
    }

    public ActivityOld getPreviousActivity() {
        return previousActivity;
    }

    public void setDocuments(List<DocumentOld> documents) {
        this.documents = documents;
    }

    public List<DocumentOld> getDocuments() {
        return documents;
    }

    public void addDocument(DocumentOld document){
        this.documents.add(document);
        document.addActivity(this);
    }
    public void removeDocument(DocumentOld document){
        this.documents.remove(document);
        document.removeActivity(this);
    }

    public void setRasciList(List<RasciOld> rasciList) {
        this.rasciList = rasciList;
    }

    public List<RasciOld> getRasciList() {
        return rasciList;
    }

    public void addRasci(RasciOld rasci) {
        this.rasciList.add(rasci);
        rasci.setActivity(this);
    }

    public void setInputs(List<InputOutput> inputs) {
        this.inputs = inputs;
    }

    public List<InputOutput> getInputs() {
        return inputs;
    }

    public void addInput(InputOutput input) {
        this.inputs.add(input);
        input.addInputActivity(this);
    }

    public void removeInput(InputOutput input) {
        this.inputs.remove(input);
        input.removeInputActivity(this);
    }

    public void setOutputs(List<InputOutput> outputs) {
        this.outputs = outputs;
    }

    public List<InputOutput> getOutputs() {
        return outputs;
    }

    public void addOutput(InputOutput output){
        this.outputs.add(output);
        output.addOutputActivity(this);
    }

    public void removeOutput(InputOutput output) {
        this.inputs.remove(output);
        output.removeOutputActivity(this);
    }
}
