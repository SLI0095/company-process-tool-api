package com.semestral_project.company_process_tool.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Activity extends Element{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long activityId;
}
