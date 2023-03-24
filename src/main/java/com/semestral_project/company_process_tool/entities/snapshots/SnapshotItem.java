package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
public class SnapshotItem {

    @JsonView(Views.Basic.class)
    private String name;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String briefDescription;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String mainDescription;
    @JsonView(Views.Basic.class)
    private String version;
    @JsonView(Views.Basic.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate changeDate;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String changeDescription;

    @JsonView(Views.Basic.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate snapshotDate;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String snapshotDescription;

    @JsonView(Views.Basic.class)
    Long originalId;

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

    public Long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }
}
