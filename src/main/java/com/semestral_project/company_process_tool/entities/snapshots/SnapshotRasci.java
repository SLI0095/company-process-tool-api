package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Role;

import javax.persistence.*;

@Entity
public class SnapshotRasci {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private SnapshotRole role;

    @JsonIgnore
    @ManyToOne
    private SnapshotTask task;

    private char type;

}
