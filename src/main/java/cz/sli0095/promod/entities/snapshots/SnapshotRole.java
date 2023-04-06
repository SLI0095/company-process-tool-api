package cz.sli0095.promod.entities.snapshots;

import cz.sli0095.promod.entities.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;
import java.util.List;

@Entity
public class SnapshotRole extends SnapshotItem{

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String skills;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String assignmentApproaches;

    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE)
    private List<SnapshotRasci> rasciList;

    @JsonIgnore
    @ManyToOne
    private Role originalRole;

    public SnapshotRole() {
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

    public List<SnapshotRasci> getRasciList() {
        return rasciList;
    }

    public void setRasciList(List<SnapshotRasci> rasciList) {
        this.rasciList = rasciList;
    }

    public Role getOriginalRole() {
        return originalRole;
    }

    public void setOriginalRole(Role originalRole) {
        this.originalRole = originalRole;
    }
}
