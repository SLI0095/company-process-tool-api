package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;
import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
public class LoginCredentials {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(unique=true,nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
