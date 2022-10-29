package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semestral_project.company_process_tool.entities.Task;

import javax.persistence.*;

@Entity
public class SnapshotTaskStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    private SnapshotTask task;

    private String name;

    @Column(columnDefinition="LONGTEXT")
    private String description;
}
