package cz.sli0095.promod.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;

@Entity
public class SnapshotTaskStep {

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    private SnapshotTask task;

    @JsonView(Views.Basic.class)
    private String name;

    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String description;

    public SnapshotTaskStep() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SnapshotTask getTask() {
        return task;
    }

    public void setTask(SnapshotTask task) {
        this.task = task;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
