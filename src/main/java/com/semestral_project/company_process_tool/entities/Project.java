package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project extends Item{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @OneToMany(mappedBy ="project", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<Element> elements = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy ="project", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<WorkItem> workItems = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy ="project", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<Role> roles = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "project_user",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> hasAccess = new ArrayList<>();

    @ManyToOne
    private User owner;

    public Project() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public List<WorkItem> getWorkItems() {
        return workItems;
    }

    public void setWorkItems(List<WorkItem> workItems) {
        this.workItems = workItems;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
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
