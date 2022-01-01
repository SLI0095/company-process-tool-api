package com.semestral_project.company_process_tool.entities;


import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonView({Views.Minimal.class,})
    private long id;

    @JsonView({Views.Minimal.class,})
    private String name;

    @JsonView({Views.Minimal.class,})
    private String surname;

//    @JsonManagedReference(value = "position")
    @JsonView(Views.Public.class)
    @ManyToOne
    private Position position;

//    @JsonManagedReference(value = "rasciList")
    @JsonView(Views.UserRasci.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RasciOld> rasciList = new ArrayList<>();

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public User(String name, String surname, Position position) {
        this.name = name;
        this.surname = surname;
        this.position = position;
    }
    public User() { }

    public long getId() {
        return id;
    }

    public String getName() {

        return name;
    }
    public void setName(String name) {

        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setRasciList(List<RasciOld> rasciList) {
        this.rasciList = rasciList;
    }

    public List<RasciOld> getRasciList() {
        return rasciList;
    }

    public void addRasci(RasciOld rasci)
    {
        rasciList.add(rasci);
        rasci.setUser(this);
    }
}
