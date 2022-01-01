package com.semestral_project.company_process_tool.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Artifact extends WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long artifactId;
}
