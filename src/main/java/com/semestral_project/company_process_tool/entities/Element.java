package com.semestral_project.company_process_tool.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="element_type",
        discriminatorType = DiscriminatorType.STRING)
public class Element extends Item{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    @JoinTable(name = "element_process",
            joinColumns = {@JoinColumn(name = "element_id")},
            inverseJoinColumns = {@JoinColumn(name = "process_id")})
    private List<Process> partOfProcess;

    @ManyToMany
    @JoinTable(name = "element_activity",
            joinColumns = {@JoinColumn(name = "element_id")},
            inverseJoinColumns = {@JoinColumn(name = "activity_id")})
    private List<Activity> partOfActivity;

    public Element() {
    }

    public long getId() {
        return id;
    }

    public List<Process> getPartOfProcess() {
        return partOfProcess;
    }

    public List<Activity> getPartOfActivity() {
        return partOfActivity;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPartOfProcess(List<Process> partOfProcess) {
        this.partOfProcess = partOfProcess;
    }

    public void setPartOfActivity(List<Activity> partOfActivity) {
        this.partOfActivity = partOfActivity;
    }
}
