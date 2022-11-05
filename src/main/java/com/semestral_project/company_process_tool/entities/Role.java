package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotElement;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Role extends Item{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition="LONGTEXT")
    private String skills;
    @Column(columnDefinition="LONGTEXT")
    private String assignmentApproaches;

    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE)
    private List<Rasci> rasciList;

//    @ManyToMany
//    @JoinTable(name = "role_task_primary",
//            joinColumns = {@JoinColumn(name = "role_id")},
//            inverseJoinColumns = {@JoinColumn(name = "element_id")})
//    private List<Task> asPrimaryPerformer;
//
//    @ManyToMany
//    @JoinTable(name = "role_task_additional",
//            joinColumns = {@JoinColumn(name = "role_id")},
//            inverseJoinColumns = {@JoinColumn(name = "element_id")})
//    private List<Task> asAdditionalPerformer;
//
//    @ManyToMany
//    @JoinTable(name = "role_user_access",
//            joinColumns = {@JoinColumn(name = "role_id")},
//            inverseJoinColumns = {@JoinColumn(name = "user_id")})
//    private List<User> hasAccess = new ArrayList<>();
//
//    @ManyToMany
//    @JoinTable(name = "role_user_edit",
//            joinColumns = {@JoinColumn(name = "role_id")},
//            inverseJoinColumns = {@JoinColumn(name = "user_id")})
//    private List<User> canEdit = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "role_task_usage",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "task_id")})
    private List<Task> canBeUsedIn = new ArrayList<>();

    @OneToMany(mappedBy ="originalRole", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<SnapshotRole> snapshots = new ArrayList<>();

//
//    @JsonIgnore
//    private Long previousId = -1L;

    public Role() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAssignmentApproaches() {
        return assignmentApproaches;
    }

    public void setAssignmentApproaches(String assignmentApproaches) {
        this.assignmentApproaches = assignmentApproaches;
    }

    public List<Rasci> getRasciList() {
        return rasciList;
    }

    public void setRasciList(List<Rasci> rasciList) {
        this.rasciList = rasciList;
    }

//    public List<Task> getAsPrimaryPerformer() {
//        return asPrimaryPerformer;
//    }
//
//    public void setAsPrimaryPerformer(List<Task> asPrimaryPerformer) {
//        this.asPrimaryPerformer = asPrimaryPerformer;
//    }
//
//    public List<Task> getAsAdditionalPerformer() {
//        return asAdditionalPerformer;
//    }
//
//    public void setAsAdditionalPerformer(List<Task> asAdditionalPerformer) {
//        this.asAdditionalPerformer = asAdditionalPerformer;
//    }
//
//    public Long getPreviousId() {
//        return previousId;
//    }
//
//    public void setPreviousId(Long previousId) {
//        this.previousId = previousId;
//    }
//
//    public List<User> getHasAccess() {
//        return hasAccess;
//    }
//
//    public void setHasAccess(List<User> hasAccess) {
//        this.hasAccess = hasAccess;
//    }
//
//    public List<User> getCanEdit() {
//        return canEdit;
//    }
//
//    public void setCanEdit(List<User> canEdit) {
//        this.canEdit = canEdit;
//    }

    public List<Task> getCanBeUsedIn() {
        return canBeUsedIn;
    }

    public void setCanBeUsedIn(List<Task> canBeUsedIn) {
        this.canBeUsedIn = canBeUsedIn;
    }

    public List<SnapshotRole> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<SnapshotRole> snapshots) {
        this.snapshots = snapshots;
    }
}
