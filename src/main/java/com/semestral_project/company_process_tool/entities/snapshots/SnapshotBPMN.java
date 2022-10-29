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

}
