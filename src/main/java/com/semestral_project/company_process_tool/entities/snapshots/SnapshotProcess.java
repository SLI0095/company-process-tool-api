package com.semestral_project.company_process_tool.entities.snapshots;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SnapshotProcess extends SnapshotElement {

    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @Column(columnDefinition="LONGTEXT")
    private String scope;
    @Column(columnDefinition="LONGTEXT")
    private String usageNotes;
    @Column(columnDefinition="LONGTEXT")
    private String alternatives;
    @Column(columnDefinition="LONGTEXT")
    private String howToStaff;
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;

    @ManyToMany(mappedBy = "partOfProcess", cascade = CascadeType.DETACH)
    private List<SnapshotElement> elements = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "snapshot_bpmn_id")
    private SnapshotBPMN workflow;

    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE)
    private List<SnapshotProcessMetric> metrics = new ArrayList<>();
}
