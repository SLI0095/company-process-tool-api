package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.*;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class InputOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal.class)
    private long id;

    @JsonView(Views.Minimal.class)
    private String name;

    @JsonView(Views.Minimal.class)
    private String state;

//    @JsonBackReference(value = "inputsActivities")
    @ManyToMany(mappedBy = "inputs")
    @JsonView(Views.InputActivites.class)
    private List<ActivityOld> inputsActivities = new ArrayList<>();

//    @JsonBackReference(value = "outputsActivities")
    @ManyToMany(mappedBy = "outputs")
    @JsonView(Views.OutputActivites.class)
    private List<ActivityOld> outputsActivities = new ArrayList<>();

//    @JsonManagedReference(value = "documents")
    @ManyToMany(mappedBy = "inputOutputs")
    @JsonView(Views.Minimal.class)
    private List<DocumentOld> documents = new ArrayList<>();

    public InputOutput(String name, String state){
        this.name = name;
        this.state = state;
    }

    public InputOutput() { }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setDocuments(List<DocumentOld> documents) {
        this.documents = documents;
    }

    public List<DocumentOld> getDocuments() {
        return documents;
    }

    public void addDocument(DocumentOld document) {
        this.documents.add(document);
        document.addInputOutput(this);
    }

    public void setInputsActivities(List<ActivityOld> inputsActivities) {
        this.inputsActivities = inputsActivities;
    }

    public List<ActivityOld> getInputsActivities() {
        return inputsActivities;
    }

    public void addInputActivity(ActivityOld activity) {
        this.inputsActivities.add(activity);
    }

    public void removeInputActivity(ActivityOld activity) {
        this.inputsActivities.remove(activity);
    }

    public void setOutputsActivities(List<ActivityOld> outputsActivities) {
        this.outputsActivities = outputsActivities;
    }

    public List<ActivityOld> getOutputsActivities() {
        return outputsActivities;
    }

    public void addOutputActivity(ActivityOld activity) {
        this.outputsActivities.add(activity);
    }

    public void removeOutputActivity(ActivityOld activity) {
        this.outputsActivities.remove(activity);
    }

    public void removeDocument(DocumentOld document){
        this.documents.remove(document);
        document.removeInputOutput(this);
    }
}
