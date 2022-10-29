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
}
