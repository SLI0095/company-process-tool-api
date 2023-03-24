package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="user_type",
        discriminatorType = DiscriminatorType.STRING)
public class UserType {

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToMany(mappedBy = "canEdit")
    private List<Item> canEditItems = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "hasAccess")
    private List<Item> hasAccessItems = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "canEdit")
    private List<Project> canEditProjects = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "hasAccess")
    private List<Project> hasAccessProjects = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Item> getCanEditItems() {
        return canEditItems;
    }

    public void setCanEditItems(List<Item> canEditItems) {
        this.canEditItems = canEditItems;
    }

    public List<Item> getHasAccessItems() {
        return hasAccessItems;
    }

    public void setHasAccessItems(List<Item> hasAccessItems) {
        this.hasAccessItems = hasAccessItems;
    }

    public List<Project> getCanEditProjects() {
        return canEditProjects;
    }

    public void setCanEditProjects(List<Project> canEditProjects) {
        this.canEditProjects = canEditProjects;
    }

    public List<Project> getHasAccessProjects() {
        return hasAccessProjects;
    }

    public void setHasAccessProjects(List<Project> hasAccessProjects) {
        this.hasAccessProjects = hasAccessProjects;
    }
}
