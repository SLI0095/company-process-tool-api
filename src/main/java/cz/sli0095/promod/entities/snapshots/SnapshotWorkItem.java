package cz.sli0095.promod.entities.snapshots;

import cz.sli0095.promod.entities.WorkItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SnapshotWorkItem extends SnapshotItem{

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String briefOutline;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String notation;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String impactOfNotHaving;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String reasonForNotNeeding;

    @JsonView(Views.Basic.class)
    private String workItemType = "";

    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String urlAddress;

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy = "workItem", cascade = CascadeType.REMOVE)
    private List<SnapshotState> workItemStates = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String templateText;


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

    @JsonIgnore
    @ManyToOne
    private WorkItem originalWorkItem;

    public SnapshotWorkItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getBriefOutline() {
        return briefOutline;
    }

    public void setBriefOutline(String briefOutline) {
        this.briefOutline = briefOutline;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getImpactOfNotHaving() {
        return impactOfNotHaving;
    }

    public void setImpactOfNotHaving(String impactOfNotHaving) {
        this.impactOfNotHaving = impactOfNotHaving;
    }

    public String getReasonForNotNeeding() {
        return reasonForNotNeeding;
    }

    public void setReasonForNotNeeding(String reasonForNotNeeding) {
        this.reasonForNotNeeding = reasonForNotNeeding;
    }

    public String getWorkItemType() {
        return workItemType;
    }

    public void setWorkItemType(String workItemType) {
        this.workItemType = workItemType;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    public List<SnapshotState> getWorkItemStates() {
        return workItemStates;
    }

    public void setWorkItemStates(List<SnapshotState> workItemStates) {
        this.workItemStates = workItemStates;
    }

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }

    public List<SnapshotTask> getAsMandatoryInput() {
        return asMandatoryInput;
    }

    public void setAsMandatoryInput(List<SnapshotTask> asMandatoryInput) {
        this.asMandatoryInput = asMandatoryInput;
    }

    public List<SnapshotTask> getAsOutput() {
        return asOutput;
    }

    public void setAsOutput(List<SnapshotTask> asOutput) {
        this.asOutput = asOutput;
    }

    public WorkItem getOriginalWorkItem() {
        return originalWorkItem;
    }

    public void setOriginalWorkItem(WorkItem originalWorkItem) {
        this.originalWorkItem = originalWorkItem;
    }
}
