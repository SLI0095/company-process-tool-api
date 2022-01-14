package com.semestral_project.company_process_tool.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Rasci {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @ManyToOne
    public Role role;

    @ManyToOne
    public Element element;

    public char type;

//    public boolean responsible;
//    public boolean accountable;
//    public boolean support;
//    public boolean consulted;
//    public boolean informed;

}
