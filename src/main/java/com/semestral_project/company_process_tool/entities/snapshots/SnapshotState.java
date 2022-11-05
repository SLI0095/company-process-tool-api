package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semestral_project.company_process_tool.entities.WorkItem;

import javax.persistence.*;

@Entity
public class SnapshotState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String stateName;
    @Column(columnDefinition="LONGTEXT")
    public String stateDescription;

    @JsonIgnore
    @ManyToOne
    private SnapshotWorkItem workItem;

    public SnapshotState() {
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

    public SnapshotWorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(SnapshotWorkItem workItem) {
        this.workItem = workItem;
    }
}
