package com.semestral_project.company_process_tool.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Document extends WorkItem{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long documentId;

    private String urlAddress;
}
