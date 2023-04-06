package cz.sli0095.promod.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.entities.snapshots.SnapshotWorkItem;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class WorkItem extends Item{

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
    private  List<State> workItemStates;

    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String templateText;

//    @JsonIgnore
//    private Long previousId = -1L;


    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "work_item_task_mandatory_input",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asMandatoryInput = new ArrayList<>();

//    @JsonIgnore
//    @ManyToMany(cascade = CascadeType.DETACH)
//    @JoinTable(name = "work_item_task_optional_input",
//            joinColumns = {@JoinColumn(name = "work_item_id")},
//            inverseJoinColumns = {@JoinColumn(name = "element_id")})
//    private List<Task> asOptionalInput = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "work_item_task_output",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asOutput = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "work_item_activity_guidance",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asGuidanceWorkItem = new ArrayList<>();

//    @OneToMany(mappedBy ="relatedWorkItem")
//    private  List<WorkItemRelation> relationsToAnotherWorkItems = new ArrayList<>();
//
//    @JsonIgnore
//    @OneToMany(mappedBy ="baseWorkItem", cascade = CascadeType.REMOVE)
//    private List<WorkItemRelation> asBase = new ArrayList<>();
//
//    @ManyToMany
//    @JoinTable(name = "work_item_user_access",
//            joinColumns = {@JoinColumn(name = "work_item_id")},
//            inverseJoinColumns = {@JoinColumn(name = "user_id")})
//    private List<User> hasAccess = new ArrayList<>();
//
//    @ManyToMany
//    @JoinTable(name = "work_item_user_edit",
//            joinColumns = {@JoinColumn(name = "work_item_id")},
//            inverseJoinColumns = {@JoinColumn(name = "user_id")})
//    private List<User> canEdit = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "work_item_task_usage",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "task_id")})
    private List<Task> canBeUsedIn = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "work_item_process_usage",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "process_id")})
    private List<Process> canBeUsedInProcesses = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy ="originalWorkItem", cascade = CascadeType.DETACH)
    private List<SnapshotWorkItem> snapshots = new ArrayList<>();

    public WorkItem() {
    }

    public List<Task> getAsMandatoryInput() {
        return asMandatoryInput;
    }

    public void setAsMandatoryInput(List<Task> asMandatoryInput) {
        this.asMandatoryInput = asMandatoryInput;
    }

//    public List<Task> getAsOptionalInput() {
//        return asOptionalInput;
//    }
//
//    public void setAsOptionalInput(List<Task> asOptionalInput) {
//        this.asOptionalInput = asOptionalInput;
//    }

    public List<Task> getAsOutput() {
        return asOutput;
    }

    public void setAsOutput(List<Task> asOutput) {
        this.asOutput = asOutput;
    }

    public List<Task> getAsGuidanceWorkItem() {
        return asGuidanceWorkItem;
    }

    public void setAsGuidanceWorkItem(List<Task> asGuidanceWorkItem) {
        this.asGuidanceWorkItem = asGuidanceWorkItem;
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

    public List<State> getWorkItemStates() {
        return workItemStates;
    }

    public void setWorkItemStates(List<State> workItemStates) {
        this.workItemStates = workItemStates;
    }

//    public List<WorkItemRelation> getRelationsToAnotherWorkItems() {
//        return relationsToAnotherWorkItems;
//    }
//
//    public void setRelationsToAnotherWorkItems(List<WorkItemRelation> relationsToAnotherWorkItems) {
//        this.relationsToAnotherWorkItems = relationsToAnotherWorkItems;
//    }
//
//    public List<WorkItemRelation> getAsBase() {
//        return asBase;
//    }
//
//    public void setAsBase(List<WorkItemRelation> asBase) {
//        this.asBase = asBase;
//    }
//
//    public Long getPreviousId() {
//        return previousId;
//    }
//
//    public void setPreviousId(Long previousId) {
//        this.previousId = previousId;
//    }
//    public List<User> getHasAccess() {
//        return hasAccess;
//    }
//
//    public void setHasAccess(List<User> hasAccess) {
//        this.hasAccess = hasAccess;
//    }
//
    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }
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

    public List<Process> getCanBeUsedInProcesses() {
        return canBeUsedInProcesses;
    }

    public void setCanBeUsedInProcesses(List<Process> canBeUsedInProcesses) {
        this.canBeUsedInProcesses = canBeUsedInProcesses;
    }

    public List<SnapshotWorkItem> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<SnapshotWorkItem> snapshots) {
        this.snapshots = snapshots;
    }
}
