package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class DocumentOld {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal.class)
    private long id;

    @JsonView(Views.Minimal.class)
    private String name;

    @JsonView(Views.Minimal.class)
    private String source;

//    @JsonBackReference(value = "activities")
    @JsonView(Views.DocumentActivities.class)
    @ManyToMany
    @JoinTable(name = "document_activity",
            joinColumns = {@JoinColumn(name = "document_id")},
            inverseJoinColumns = {@JoinColumn(name = "activity_id")})
    private List<ActivityOld> activities = new ArrayList<>();

//    @JsonBackReference(value = "inputOutputs")
    @JsonView(Views.DocumentInputOutputs.class)
    @ManyToMany
    @JoinTable(name = "document_input_output",
            joinColumns = {@JoinColumn(name = "document_id")},
            inverseJoinColumns = {@JoinColumn(name = "input_output_id")})
    private List<InputOutput> inputOutputs = new ArrayList<>();

    public DocumentOld(String name, String source){
        this.name = name;
        this.source = source;
    }

    public DocumentOld() { }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setActivities(List<ActivityOld> activities) {
        this.activities = activities;
    }

    public List<ActivityOld> getActivities() {
        return activities;
    }

    public void addActivity(ActivityOld activity) {
        this.activities.add(activity);
    }
    public void removeActivity(ActivityOld activity) {
        this.activities.remove(activity);
    }

    public List<InputOutput> getInputOutputs() {
        return inputOutputs;
    }

    public void setInputOutputs(List<InputOutput> inputOutputs) {
        this.inputOutputs = inputOutputs;
    }

    public void addInputOutput(InputOutput inputOutput){
        this.inputOutputs.add(inputOutput);
    }
    public void removeInputOutput(InputOutput inputOutput) {
        this.inputOutputs.remove(inputOutput);
    }

}
