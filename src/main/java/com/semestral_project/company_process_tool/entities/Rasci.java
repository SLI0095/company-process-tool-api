package com.semestral_project.company_process_tool.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Rasci {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long rasciId;

    public Role role;

    public char type;

    public boolean responsible;
    public boolean accountable;
    public boolean support;
    public boolean consulted;
    public boolean informed;

}
