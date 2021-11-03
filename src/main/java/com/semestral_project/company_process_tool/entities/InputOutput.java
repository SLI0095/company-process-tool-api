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
    private List<Activity> inputsActivities = new ArrayList<>();

//    @JsonBackReference(value = "outputsActivities")
    @ManyToMany(mappedBy = "outputs")
    @JsonView(Views.OutputActivites.class)
    private List<Activity> outputsActivities = new ArrayList<>();

//    @JsonManagedReference(value = "documents")
    @ManyToMany(mappedBy = "inputOutputs")
    @JsonView(Views.InputOutputDocuments.class)
    private List<Document> documents = new ArrayList<>();

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

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void addDocument(Document document) {
        this.documents.add(document);
    }

    public void setInputsActivities(List<Activity> inputsActivities) {
        this.inputsActivities = inputsActivities;
    }

    public List<Activity> getInputsActivities() {
        return inputsActivities;
    }

    public void addInputActivity(Activity activity) {
        this.inputsActivities.add(activity);
    }

    public void removeInputActivity(Activity activity) {
        this.inputsActivities.remove(activity);
    }

    public void setOutputsActivities(List<Activity> outputsActivities) {
        this.outputsActivities = outputsActivities;
    }

    public List<Activity> getOutputsActivities() {
        return outputsActivities;
    }

    public void addOutputActivity(Activity activity) {
        this.outputsActivities.add(activity);
    }

    public void removeOutputActivity(Activity activity) {
        this.outputsActivities.remove(activity);
    }
}
