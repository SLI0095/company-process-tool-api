package com.semestral_project.company_process_tool.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class RemovedArtifact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long oldArtifactId;
}
