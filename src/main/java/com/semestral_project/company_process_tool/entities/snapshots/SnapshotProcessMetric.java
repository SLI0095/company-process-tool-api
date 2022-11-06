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

    public SnapshotProcessMetric() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SnapshotProcess getProcess() {
        return process;
    }

    public void setProcess(SnapshotProcess process) {
        this.process = process;
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
}
