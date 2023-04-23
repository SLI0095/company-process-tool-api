package cz.sli0095.promod.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.entities.snapshots.SnapshotRole;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Role extends Item{

    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String skills;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String assignmentApproaches;

    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE)
    private List<Rasci> rasciList;
    @ManyToMany
    @JoinTable(name = "role_task_usage",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "task_id")})
    private List<Task> canBeUsedIn = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy ="originalRole", cascade = CascadeType.DETACH)
    private List<SnapshotRole> snapshots = new ArrayList<>();

    public Role() {
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
