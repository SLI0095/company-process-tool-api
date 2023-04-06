package cz.sli0095.promod.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;

@Entity
public class Rasci {

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Basic.class)
    @ManyToOne
    private Role role;

    @JsonIgnore
    @ManyToOne
    private Task task;

    @JsonView(Views.Basic.class)
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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
