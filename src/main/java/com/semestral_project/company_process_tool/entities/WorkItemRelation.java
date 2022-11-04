//package com.semestral_project.company_process_tool.entities;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import javax.persistence.*;
//
//@Entity
//public class WorkItemRelation {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @ManyToOne
//    private WorkItem baseWorkItem;
//
//    @JsonIgnore
//    @ManyToOne
//    private WorkItem relatedWorkItem;
//
//    private String relationType;
//
//    public WorkItemRelation() {
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public WorkItem getBaseWorkItem() {
//        return baseWorkItem;
//    }
//
//    public void setBaseWorkItem(WorkItem baseWorkItem) {
//        this.baseWorkItem = baseWorkItem;
//    }
//
//    public WorkItem getRelatedWorkItem() {
//        return relatedWorkItem;
//    }
//
//    public void setRelatedWorkItem(WorkItem relatedWorkItem) {
//        this.relatedWorkItem = relatedWorkItem;
//    }
//
//    public String getRelationType() {
//        return relationType;
//    }
//
//    public void setRelationType(String relationType) {
//        this.relationType = relationType;
//    }
//}
