package com.semestral_project.company_process_tool.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public class Item {

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



    public Item() {
    }

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
}
