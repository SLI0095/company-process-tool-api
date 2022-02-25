package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.repositories.StateRepository;
import com.semestral_project.company_process_tool.repositories.WorkItemRelationRepository;
import com.semestral_project.company_process_tool.repositories.WorkItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WorkItemService {

    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    WorkItemRelationRepository workItemRelationRepository;
    @Autowired
    StateRepository stateRepository;
    @Autowired
    BPMNparser bpmNparser;


    public WorkItem fillWorkItem(WorkItem oldWorkItem, WorkItem updatedWorkItem){
        oldWorkItem.setName(updatedWorkItem.getName());
        oldWorkItem.setBriefDescription(updatedWorkItem.getBriefDescription());
        oldWorkItem.setMainDescription(updatedWorkItem.getMainDescription());
        oldWorkItem.setVersion(updatedWorkItem.getVersion());
        oldWorkItem.setChangeDate(updatedWorkItem.getChangeDate());
        oldWorkItem.setChangeDescription(updatedWorkItem.getChangeDescription());
        oldWorkItem.setPurpose(updatedWorkItem.getPurpose());
        oldWorkItem.setKeyConsiderations(updatedWorkItem.getKeyConsiderations());
        oldWorkItem.setBriefOutline(updatedWorkItem.getBriefOutline());
        oldWorkItem.setNotation(updatedWorkItem.getNotation());
        oldWorkItem.setImpactOfNotHaving(updatedWorkItem.getImpactOfNotHaving());
        oldWorkItem.setReasonForNotNeeding(updatedWorkItem.getReasonForNotNeeding());
        oldWorkItem.setWorkItemType(updatedWorkItem.getWorkItemType());
        oldWorkItem.setUrlAddress(updatedWorkItem.getUrlAddress());
        oldWorkItem.setTemplateText(updatedWorkItem.getTemplateText());
        return oldWorkItem;
    }

    public List<WorkItem> getAllWorkItems(){
        try {
            return (List<WorkItem>) workItemRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public WorkItem getWorkItemById(long id){
        Optional<WorkItem> workItemData = workItemRepository.findById(id);
        if(workItemData.isPresent()) {
            return workItemData.get();
        } else return null;
    }

    public boolean addWorkItem(WorkItem workItem){
        try {
            workItemRepository.save(workItem);
            return true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteWorkItem(long id){
        try {
            if(bpmNparser.removeWorkItemFromAllWorkflows(workItemRepository.findById(id).get()))
            {
                workItemRepository.deleteById(id);
                return true;
            }
            return false;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int updateWorkItem(long id, WorkItem workItem) {
        Optional<WorkItem> workItemData = workItemRepository.findById(id);

        if (workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            String oldName = workItem_.getName();
            workItem_ = fillWorkItem(workItem_, workItem);
            workItemRepository.save(workItem_);
            bpmNparser.updateWorkItemInAllWorkflows(workItem_,true,null);
            return 1;
        } else return 2;
    }

    public int addWorkItemState(long id, State state){
        Optional<WorkItem> workItemData = workItemRepository.findById(id);
        if(workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            state.setWorkItem(workItem_);
            stateRepository.save(state);

            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int removeWorkItemState(long id, State state){
        Optional<WorkItem> workItemData = workItemRepository.findById(id);
        if(workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            State state_ = stateRepository.findById(state.getId()).get();
            if(state_.getWorkItem().getId() == workItem_.getId())
            {
                stateRepository.delete(state_);
                return 1;
            }
            else {
                return 3;
            }

        }
        else
        {
            return 2;
        }
    }

    public int addRelationToWorkItem(long id, WorkItem workItem, String relationType){
        if(id == workItem.getId())
            return 4;
        Optional<WorkItem> workItemData = workItemRepository.findById(id);

        if(workItemData.isPresent()){
            WorkItem workItem_ = workItemData.get();

            List<WorkItemRelation> relations = workItem_.getRelationsToAnotherWorkItems();
            for(WorkItemRelation relation : relations){
                if(relation.getBaseWorkItem().getId() == workItem.getId()){
                    return 3;
                }
            }
            WorkItemRelation relation = new WorkItemRelation();
            relation.setBaseWorkItem(workItem);
            relation.setRelatedWorkItem(workItem_);
            relation.setRelationType(relationType);
            relation = workItemRelationRepository.save(relation);
            relations.add(relation);
            workItem_.setRelationsToAnotherWorkItems(relations);
            workItemRepository.save(workItem_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int removeRelationFromWorkItem(long id, WorkItemRelation workItemRelation){
        Optional<WorkItem> workItemData = workItemRepository.findById(id);

        if(workItemData.isPresent()){
            WorkItem workItem_ = workItemData.get();
            WorkItemRelation artifactRelation_ = workItemRelationRepository.findById(workItemRelation.getId()).get();

            List<WorkItemRelation> relations = workItem_.getRelationsToAnotherWorkItems();
            relations.remove(artifactRelation_);
            workItem_.setRelationsToAnotherWorkItems(relations);
            workItemRepository.save(workItem_);
            workItemRelationRepository.delete(artifactRelation_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public List<WorkItem> getAllTemplates(){
        List<WorkItem> allTemplates = workItemRepository.findAllWorkItemTemplate();
        return allTemplates;
    }
}
