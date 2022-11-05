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

    public SnapshotRasci() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SnapshotRole getRole() {
        return role;
    }

    public void setRole(SnapshotRole role) {
        this.role = role;
    }

    public SnapshotTask getTask() {
        return task;
    }

    public void setTask(SnapshotTask task) {
        this.task = task;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }
}
