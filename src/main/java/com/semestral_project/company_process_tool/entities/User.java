package com.semestral_project.company_process_tool.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.DETACH)
    private List<Element> asOwnerElements = new ArrayList<>();

    @ManyToMany(mappedBy = "hasAccess")
    private List<Element> hasAccessElements = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.DETACH)
    private List<Project> asOwnerProjects = new ArrayList<>();

    @ManyToMany(mappedBy = "hasAccess")
    private List<Project> hasAccessProjects = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.DETACH)
    private List<Role> asOwnerRoles = new ArrayList<>();

    @ManyToMany(mappedBy = "hasAccess")
    private List<Role> hasAccessRoles = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.DETACH)
    private List<WorkItem> asOwnerWorkItems = new ArrayList<>();

    @ManyToMany(mappedBy = "hasAccess")
    private List<WorkItem> hasAccessWorkItems = new ArrayList<>();

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Element> getAsOwnerElements() {
        return asOwnerElements;
    }

    public void setAsOwnerElements(List<Element> asOwnerElements) {
        this.asOwnerElements = asOwnerElements;
    }

    public List<Element> getHasAccessElements() {
        return hasAccessElements;
    }

    public void setHasAccessElements(List<Element> hasAccessElements) {
        this.hasAccessElements = hasAccessElements;
    }

    public List<Project> getAsOwnerProjects() {
        return asOwnerProjects;
    }

    public void setAsOwnerProjects(List<Project> asOwnerProjects) {
        this.asOwnerProjects = asOwnerProjects;
    }

    public List<Project> getHasAccessProjects() {
        return hasAccessProjects;
    }

    public void setHasAccessProjects(List<Project> hasAccessProjects) {
        this.hasAccessProjects = hasAccessProjects;
    }

    public List<Role> getAsOwnerRoles() {
        return asOwnerRoles;
    }

    public void setAsOwnerRoles(List<Role> asOwnerRoles) {
        this.asOwnerRoles = asOwnerRoles;
    }

    public List<Role> getHasAccessRoles() {
        return hasAccessRoles;
    }

    public void setHasAccessRoles(List<Role> hasAccessRoles) {
        this.hasAccessRoles = hasAccessRoles;
    }

    public List<WorkItem> getAsOwnerWorkItems() {
        return asOwnerWorkItems;
    }

    public void setAsOwnerWorkItems(List<WorkItem> asOwnerWorkItems) {
        this.asOwnerWorkItems = asOwnerWorkItems;
    }

    public List<WorkItem> getHasAccessWorkItems() {
        return hasAccessWorkItems;
    }

    public void setHasAccessWorkItems(List<WorkItem> hasAccessWorkItems) {
        this.hasAccessWorkItems = hasAccessWorkItems;
    }
}
