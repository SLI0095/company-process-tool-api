package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semestral_project.company_process_tool.entities.WorkItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SnapshotWorkItem extends SnapshotItem{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;
    @Column(columnDefinition="LONGTEXT")
    private String briefOutline;
    @Column(columnDefinition="LONGTEXT")
    private String notation;
    @Column(columnDefinition="LONGTEXT")
    private String impactOfNotHaving;
    @Column(columnDefinition="LONGTEXT")
    private String reasonForNotNeeding;

    private String workItemType = "";

    @Column(columnDefinition="LONGTEXT")
    private String urlAddress;

    @OneToMany(mappedBy = "workItem", cascade = CascadeType.REMOVE)
    private List<SnapshotState> workItemStates;

    @Column(columnDefinition="LONGTEXT")
    private String templateText;

    @JsonIgnore
    private Long previousId = -1L;


    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "snapshot_work_item_snapshot_task_input",
            joinColumns = {@JoinColumn(name = "snapshot_work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "snapshot_element_id")})
    private List<SnapshotTask> asMandatoryInput = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "snapshot_work_item_snapshot_task_output",
            joinColumns = {@JoinColumn(name = "snapshot_work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "snapshot_element_id")})
    private List<SnapshotTask> asOutput = new ArrayList<>();

    @ManyToOne
    private WorkItem originalWorkItem;
}
