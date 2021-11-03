package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.semestral_project.company_process_tool.utils.Views;

import javax.persistence.*;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Rasci {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal.class)
    private long id;

    @JsonView(Views.Minimal.class)
    private String roleType;

//    @JsonBackReference(value = "user")
    @JsonView(Views.Minimal.class)
    @ManyToOne
    private User user;

//    @JsonBackReference(value = "activity")
    @JsonView(Views.RasciGeneral.class)
    @ManyToOne
    private Activity activity;

    public Rasci() { }

    public Rasci(User user, Activity activity, String role_type)
    {
        this.user = user;
        this.activity = activity;
        this.roleType = role_type;
    }

    public long getId() {
        return id;
    }

    public void setRole_type(String roleType) {
        this.roleType = roleType;
    }

    public String getRole_type() {
        return roleType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
