package cz.sli0095.promod.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;

@Entity
public class SnapshotProcessMetric {

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    private SnapshotProcess process;

    @JsonView(Views.Basic.class)
    private String name;

    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String description;

    public SnapshotProcessMetric() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SnapshotProcess getProcess() {
        return process;
    }

    public void setProcess(SnapshotProcess process) {
        this.process = process;
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
