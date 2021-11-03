package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Position {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonView(Views.Minimal.class)
    private long id;

    @JsonView(Views.Minimal.class)
    private String name;

//    @JsonBackReference(value = "users")
    @JsonView(Views.PositionUsers.class)
    @OneToMany(mappedBy = "position",cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    public Position(String name) {
        this.name = name;
    }

    public Position() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user)
    {
        this.users.add(user);
        user.setPosition(this);
    }
}