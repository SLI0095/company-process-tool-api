package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;

@Entity
public class SnapshotRasci {

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Basic.class)
    @ManyToOne
    private SnapshotRole role;

    @JsonIgnore
    @ManyToOne
    private SnapshotTask task;

    @JsonView(Views.Basic.class)
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
