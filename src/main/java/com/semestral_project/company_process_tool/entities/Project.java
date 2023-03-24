package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Basic.class)
    private String name;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String briefDescription;

    @JsonView(Views.Basic.class)
    @ManyToMany
    @JoinTable(name = "project_user_access",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<UserType> hasAccess = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @ManyToMany
    @JoinTable(name = "project_user_edit",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<UserType> canEdit = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private List<Item> projectItems = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @ManyToOne
    private User projectOwner;

    public Project() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public List<UserType> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(List<UserType> hasAccess) {
        this.hasAccess = hasAccess;
    }

    public List<UserType> getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(List<UserType> canEdit) {
        this.canEdit = canEdit;
    }

    public List<Item> getProjectItems() {
        return projectItems;
    }

    public void setProjectItems(List<Item> projectItems) {
        this.projectItems = projectItems;
    }

    public User getProjectOwner() {
        return projectOwner;
    }

    public void setProjectOwner(User projectOwner) {
        this.projectOwner = projectOwner;
    }
}
