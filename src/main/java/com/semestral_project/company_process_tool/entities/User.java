package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    @ManyToMany(mappedBy = "canEdit")
    private List<Element> canEditElements = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "hasAccess")
    private List<Element> hasAccessElements = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "canEdit")
    private List<Role> canEditRoles = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "hasAccess")
    private List<Role> hasAccessRoles = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "canEdit")
    private List<WorkItem> canEditWorkItems = new ArrayList<>();

    @JsonIgnore
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

    public List<Element> getHasAccessElements() {
        return hasAccessElements;
    }

    public void setHasAccessElements(List<Element> hasAccessElements) {
        this.hasAccessElements = hasAccessElements;
    }

    public List<Role> getHasAccessRoles() {
        return hasAccessRoles;
    }

    public void setHasAccessRoles(List<Role> hasAccessRoles) {
        this.hasAccessRoles = hasAccessRoles;
    }

    public List<WorkItem> getHasAccessWorkItems() {
        return hasAccessWorkItems;
    }

    public void setHasAccessWorkItems(List<WorkItem> hasAccessWorkItems) {
        this.hasAccessWorkItems = hasAccessWorkItems;
    }

    public List<Element> getCanEditElements() {
        return canEditElements;
    }

    public void setCanEditElements(List<Element> canEditElements) {
        this.canEditElements = canEditElements;
    }

    public List<Role> getCanEditRoles() {
        return canEditRoles;
    }

    public void setCanEditRoles(List<Role> canEditRoles) {
        this.canEditRoles = canEditRoles;
    }

    public List<WorkItem> getCanEditWorkItems() {
        return canEditWorkItems;
    }

    public void setCanEditWorkItems(List<WorkItem> canEditWorkItems) {
        this.canEditWorkItems = canEditWorkItems;
    }
}
