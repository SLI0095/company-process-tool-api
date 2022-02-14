package com.semestral_project.company_process_tool.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "work_item")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name="work_item_type",
//        discriminatorType = DiscriminatorType.STRING)
public class WorkItem extends Item{

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
    private  List<State> workItemStates;

    @JsonIgnore
    @ManyToOne
    private Project project = null;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "work_item_task_mandatory_input",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asMandatoryInput;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "work_item_task_optional_input",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asOptionalInput;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "work_item_task_output",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asOutput;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "work_item_activity_guidance",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asGuidanceWorkItem;

    @OneToMany(mappedBy ="relatedWorkItem")
    private  List<WorkItemRelation> relationsToAnotherWorkItems;

    @JsonIgnore
    @OneToMany(mappedBy ="baseWorkItem", cascade = CascadeType.REMOVE)
    private List<WorkItemRelation> asBase;

    public WorkItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Task> getAsMandatoryInput() {
        return asMandatoryInput;
    }

    public void setAsMandatoryInput(List<Task> asMandatoryInput) {
        this.asMandatoryInput = asMandatoryInput;
    }

    public List<Task> getAsOptionalInput() {
        return asOptionalInput;
    }

    public void setAsOptionalInput(List<Task> asOptionalInput) {
        this.asOptionalInput = asOptionalInput;
    }

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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<WorkItemRelation> getRelationsToAnotherWorkItems() {
        return relationsToAnotherWorkItems;
    }

    public void setRelationsToAnotherWorkItems(List<WorkItemRelation> relationsToAnotherWorkItems) {
        this.relationsToAnotherWorkItems = relationsToAnotherWorkItems;
    }

    public List<WorkItemRelation> getAsBase() {
        return asBase;
    }

    public void setAsBase(List<WorkItemRelation> asBase) {
        this.asBase = asBase;
    }
}
