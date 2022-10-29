package com.semestral_project.company_process_tool.entities.snapshots;

import com.semestral_project.company_process_tool.entities.Rasci;
import com.semestral_project.company_process_tool.entities.TaskStep;
import com.semestral_project.company_process_tool.entities.WorkItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SnapshotTask extends SnapshotElement{

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<SnapshotTaskStep> steps = new ArrayList<>();
    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;

    private String taskType = "task";

    @ManyToMany(mappedBy = "asInput")
    private List<SnapshotWorkItem> mandatoryInputs = new ArrayList<>();

    @ManyToMany(mappedBy = "asOutput")
    private List<SnapshotWorkItem> outputs = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<SnapshotRasci> rasciList = new ArrayList<>();
}
