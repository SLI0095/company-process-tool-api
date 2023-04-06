package cz.sli0095.promod.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SnapshotTask extends SnapshotElement{

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<SnapshotTaskStep> steps = new ArrayList<>();
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;

    @JsonView(Views.Basic.class)
    private String taskType = "task";

    @JsonView(Views.Basic.class)
    @ManyToMany(mappedBy = "asMandatoryInput")
    private List<SnapshotWorkItem> mandatoryInputs = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @ManyToMany(mappedBy = "asOutput")
    private List<SnapshotWorkItem> outputs = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<SnapshotRasci> rasciList = new ArrayList<>();

    public SnapshotTask() {
    }

    public List<SnapshotTaskStep> getSteps() {
        return steps;
    }

    public void setSteps(List<SnapshotTaskStep> steps) {
        this.steps = steps;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getKeyConsiderations() {
        return keyConsiderations;
    }

    public void setKeyConsiderations(String keyConsiderations) {
        this.keyConsiderations = keyConsiderations;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public List<SnapshotWorkItem> getMandatoryInputs() {
        return mandatoryInputs;
    }

    public void setMandatoryInputs(List<SnapshotWorkItem> mandatoryInputs) {
        this.mandatoryInputs = mandatoryInputs;
    }

    public List<SnapshotWorkItem> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<SnapshotWorkItem> outputs) {
        this.outputs = outputs;
    }

    public List<SnapshotRasci> getRasciList() {
        return rasciList;
    }

    public void setRasciList(List<SnapshotRasci> rasciList) {
        this.rasciList = rasciList;
    }
}
