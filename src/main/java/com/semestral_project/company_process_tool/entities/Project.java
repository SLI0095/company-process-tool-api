package com.semestral_project.company_process_tool.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project extends Item{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy ="project", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<Process> processes = new ArrayList<>();

    public Project() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }
}
