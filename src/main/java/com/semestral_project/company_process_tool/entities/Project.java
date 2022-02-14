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
    private List<Element> elements = new ArrayList<>();

    @OneToMany(mappedBy ="project", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<WorkItem> workItems = new ArrayList<>();

    @OneToMany(mappedBy ="project", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<Role> roles = new ArrayList<>();

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
}
