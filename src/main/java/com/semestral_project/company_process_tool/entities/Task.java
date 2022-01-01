package com.semestral_project.company_process_tool.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Task extends Element{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long taskId;

}
