package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Rasci {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Role role;

    @JsonIgnore
    @ManyToOne
    private Element element;

    private char type;

    public Rasci() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    //    public boolean responsible;
//    public boolean accountable;
//    public boolean support;
//    public boolean consulted;
//    public boolean informed;

}
