package com.semestral_project.company_process_tool.entities.snapshots;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
public class SnapshotItem {

    private String name;
    @Column(columnDefinition="LONGTEXT")
    private String briefDescription;
    @Column(columnDefinition="LONGTEXT")
    private String mainDescription;
    private String version;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate changeDate;
    @Column(columnDefinition="LONGTEXT")
    private String changeDescription;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate snapshotDate;
    @Column(columnDefinition="LONGTEXT")
    private String snapshotDescription;
}
