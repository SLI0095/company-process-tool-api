package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="element_type",
        discriminatorType = DiscriminatorType.STRING)
public class Element extends Item{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "element_process",
            joinColumns = {@JoinColumn(name = "element_id")},
            inverseJoinColumns = {@JoinColumn(name = "process_id")})
    private List<Process> partOfProcess = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "element_user_access",
            joinColumns = {@JoinColumn(name = "element_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> hasAccess = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "element_user_edit",
            joinColumns = {@JoinColumn(name = "element_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> canEdit = new ArrayList<>();

    @ManyToOne
    private Project project = null;

    @JsonIgnore
    private Long previousId = -1L;


    public Element() {
    }

    public long getId() {
        return id;
    }

    public List<Process> getPartOfProcess() {
        return partOfProcess;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPartOfProcess(List<Process> partOfProcess) {
        this.partOfProcess = partOfProcess;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getPreviousId() {
        return previousId;
    }

    public void setPreviousId(Long previousId) {
        this.previousId = previousId;
    }

    public List<User> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(List<User> hasAccess) {
        this.hasAccess = hasAccess;
    }

    public List<User> getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(List<User> canEdit) {
        this.canEdit = canEdit;
    }
}
