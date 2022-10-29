package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semestral_project.company_process_tool.entities.Process;

import javax.persistence.*;

@Entity
public class SnapshotProcessMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    private SnapshotProcess process;

    private String name;

    @Column(columnDefinition="LONGTEXT")
    private String description;
}
