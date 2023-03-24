package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("user")
public class User extends UserType {

    @JsonView(Views.Basic.class)
    private String username;
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private List<Item> isOwner = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "projectOwner")
    private List<Project> isProjectOwner = new ArrayList<>();


    @JsonView(Views.UsersGroups.class)
    @OneToMany(mappedBy = "creator")
    private List<UserGroup> isCreator = new ArrayList<>();


    @JsonView(Views.UsersGroups.class)
    @ManyToMany(mappedBy = "users", cascade = CascadeType.DETACH)
    private List<UserGroup> groups = new ArrayList<>();

    public User() {
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

    public List<Item> getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(List<Item> isOwner) {
        this.isOwner = isOwner;
    }

    public List<UserGroup> getIsCreator() {
        return isCreator;
    }

    public void setIsCreator(List<UserGroup> isCreator) {
        this.isCreator = isCreator;
    }


    public List<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
    }

    public List<Project> getIsProjectOwner() {
        return isProjectOwner;
    }

    public void setIsProjectOwner(List<Project> isProjectOwner) {
        this.isProjectOwner = isProjectOwner;
    }
}
