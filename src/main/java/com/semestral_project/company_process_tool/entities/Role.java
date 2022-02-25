package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Role extends Item{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition="LONGTEXT")
    private String skills;
    @Column(columnDefinition="LONGTEXT")
    private String assignmentApproaches;

    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE)
    private List<Rasci> rasciList;

    @ManyToMany
    @JoinTable(name = "role_task_primary",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asPrimaryPerformer;

    @ManyToMany
    @JoinTable(name = "role_task_additional",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asAdditionalPerformer;

    @ManyToMany
    @JoinTable(name = "role_user",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> hasAccess = new ArrayList<>();

    @ManyToOne
    private User owner;


    @ManyToOne
    private Project project = null;

    @JsonIgnore
    private Long previousId = -1L;

    public Role() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAssignmentApproaches() {
        return assignmentApproaches;
    }

    public void setAssignmentApproaches(String assignmentApproaches) {
        this.assignmentApproaches = assignmentApproaches;
    }

    public List<Rasci> getRasciList() {
        return rasciList;
    }

    public void setRasciList(List<Rasci> rasciList) {
        this.rasciList = rasciList;
    }

    public List<Task> getAsPrimaryPerformer() {
        return asPrimaryPerformer;
    }

    public void setAsPrimaryPerformer(List<Task> asPrimaryPerformer) {
        this.asPrimaryPerformer = asPrimaryPerformer;
    }

    public List<Task> getAsAdditionalPerformer() {
        return asAdditionalPerformer;
    }

    public void setAsAdditionalPerformer(List<Task> asAdditionalPerformer) {
        this.asAdditionalPerformer = asAdditionalPerformer;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
