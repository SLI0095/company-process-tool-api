package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//@Entity
public class ProcessOld {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonView(Views.Minimal.class)
    private long id;

    @JsonView(Views.Minimal.class)
    private String name;

//    @JsonManagedReference(value = "activities")
    @JsonView(Views.ProcessGeneral.class)
    @OneToMany(mappedBy ="process", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityOld> activities = new ArrayList<>();

    public ProcessOld() {};
    public ProcessOld(String name){
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addActivity(ActivityOld activity){
        activities.add(activity);
        activity.setProcess(this);
    }

    public void setActivities(List<ActivityOld> activities) {
        this.activities = activities;
    }

    public List<ActivityOld> getActivities() {
        return activities;
    }
}
