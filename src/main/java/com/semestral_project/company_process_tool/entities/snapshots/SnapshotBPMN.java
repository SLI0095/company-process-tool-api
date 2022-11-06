package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semestral_project.company_process_tool.entities.Process;

import javax.persistence.*;
@Entity
public class SnapshotBPMN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    private String bpmnContent;

    @JsonIgnore
    @OneToOne(mappedBy = "workflow")
    private SnapshotProcess process;

    public SnapshotBPMN() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBpmnContent() {
        return bpmnContent;
    }

    public void setBpmnContent(String bpmnContent) {
        this.bpmnContent = bpmnContent;
    }

    public SnapshotProcess getProcess() {
        return process;
    }

    public void setProcess(SnapshotProcess process) {
        this.process = process;
    }
}
