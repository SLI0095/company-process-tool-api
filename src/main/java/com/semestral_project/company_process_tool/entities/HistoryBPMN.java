package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class HistoryBPMN {

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    private Process process;

    @JsonView(Views.Basic.class)
    @Lob
    private String bpmnContent;

    @JsonView(Views.Basic.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime changeDate;

    public HistoryBPMN() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public String getBpmnContent() {
        return bpmnContent;
    }

    public void setBpmnContent(String bpmnContent) {
        this.bpmnContent = bpmnContent;
    }
}
