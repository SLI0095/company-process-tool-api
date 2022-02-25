package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String stateName;
    @Column(columnDefinition="LONGTEXT")
    public String stateDescription;

    @JsonIgnore
    @ManyToOne
    private WorkItem workItem;


    public State() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(String stateDescription) {
        this.stateDescription = stateDescription;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }
}
