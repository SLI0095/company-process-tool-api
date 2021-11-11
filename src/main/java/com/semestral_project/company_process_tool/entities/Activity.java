package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.*;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Activity {

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
    private Activity previousActivity;

//    @JsonManagedReference(value = "nextActivities")
    @OneToMany(mappedBy = "previousActivity")
    @JsonView(Views.ProcessRender.class)
    private List<Activity> nextActivities = new ArrayList<>();

//    @JsonBackReference(value = "process")
    @ManyToOne(optional = false)
    @JsonView(Views.ActivityGeneral.class)
    private Process process;

//    @JsonManagedReference(value = "rasciList")
    @JsonView(Views.ActivityRasci.class)
    @OneToMany(mappedBy = "activity",orphanRemoval = true)
    private List<Rasci> rasciList = new ArrayList<>();

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
    private List<Document> documents = new ArrayList<>();

    public Activity() { }

    public Activity(String name, String description, Process process, Activity previousActivity) {
        this.name = name;
        this.description = description;
        this.process = process;
        this.previousActivity = previousActivity;
    }

    public Activity(String name, String description, Process process) {
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

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public void setNextActivities(List<Activity> nextActivities) {
        this.nextActivities = nextActivities;
    }

    public List<Activity> getNextActivities() {
        return nextActivities;
    }

    public void addNextActivity(Activity activity)
    {
        this.nextActivities.add(activity);
        activity.setPreviousActivity(activity);
    }

    public void setPreviousActivity(Activity previousActivity) {
        this.previousActivity = previousActivity;
    }

    public Activity getPreviousActivity() {
        return previousActivity;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void addDocument(Document document){
        this.documents.add(document);
        document.addActivity(this);
    }
    public void removeDocument(Document document){
        this.documents.remove(document);
        document.removeActivity(this);
    }

    public void setRasciList(List<Rasci> rasciList) {
        this.rasciList = rasciList;
    }

    public List<Rasci> getRasciList() {
        return rasciList;
    }

    public void addRasci(Rasci rasci) {
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
