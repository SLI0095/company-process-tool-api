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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getMainDescription() {
        return mainDescription;
    }

    public void setMainDescription(String mainDescription) {
        this.mainDescription = mainDescription;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDate changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(LocalDate snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public String getSnapshotDescription() {
        return snapshotDescription;
    }

    public void setSnapshotDescription(String snapshotDescription) {
        this.snapshotDescription = snapshotDescription;
    }
}
