package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semestral_project.company_process_tool.entities.Rasci;
import com.semestral_project.company_process_tool.entities.Role;

import javax.persistence.*;
import java.util.List;

@Entity
public class SnapshotRole extends SnapshotItem{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition="LONGTEXT")
    private String skills;
    @Column(columnDefinition="LONGTEXT")
    private String assignmentApproaches;

    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE)
    private List<SnapshotRasci> rasciList;

    @ManyToOne
    private Role originalRole;
}
