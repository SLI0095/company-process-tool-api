package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotElement;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="element_type",
        discriminatorType = DiscriminatorType.STRING)
public class Element extends Item{

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "element_process",
            joinColumns = {@JoinColumn(name = "element_id")},
            inverseJoinColumns = {@JoinColumn(name = "process_id")})
    private List<Process> partOfProcess = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "element_process_usage",
            joinColumns = {@JoinColumn(name = "element_id")},
            inverseJoinColumns = {@JoinColumn(name = "process_id")})
    private List<Process> canBeUsedIn = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy ="originalElement", cascade = CascadeType.DETACH)
    private List<SnapshotElement> snapshots = new ArrayList<>();

    public Element() {
    }

    public List<Process> getPartOfProcess() {
        return partOfProcess;
    }

    public void setPartOfProcess(List<Process> partOfProcess) {
        this.partOfProcess = partOfProcess;
    }

    public List<Process> getCanBeUsedIn() {
        return canBeUsedIn;
    }

    public void setCanBeUsedIn(List<Process> canBeUsedIn) {
        this.canBeUsedIn = canBeUsedIn;
    }

    public List<SnapshotElement> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<SnapshotElement> snapshots) {
        this.snapshots = snapshots;
    }
}
