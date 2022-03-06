package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.repositories.StateRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
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
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectService projectService;


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

    public long addWorkItem(WorkItem workItem, long userId){
        try {
            if(userRepository.existsById(userId)) {
                User user = userRepository.findById(userId).get();
                if(workItem.getProject() != null){
                    Project project = projectService.getProjectById(workItem.getProject().getId());
                    if(project.getCanEdit().contains(user)){
                        var list = workItem.getCanEdit();
                        list.add(user);
                        workItem = workItemRepository.save(workItem);
                        return workItem.getId();
                    } else {
                        return -1;
                    }
                }
                var list = workItem.getCanEdit();
                list.add(user);
                workItem = workItemRepository.save(workItem);
                return workItem.getId();
            }
            else return -1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean deleteWorkItem(long id, long whoEdits){
        try {
            Optional<WorkItem> workItemData = workItemRepository.findById(id);
            if(workItemData.isPresent()) {
                WorkItem workItem_ = workItemData.get();
                User whoEdits_ = userRepository.findById(whoEdits).get();
                    if (workItem_.getCanEdit().contains(whoEdits_)){
                        if (bpmNparser.removeWorkItemFromAllWorkflows(workItemRepository.findById(id).get())) {
                            workItemRepository.deleteById(id);
                            return true;
                        }
                    }
                }
                return false;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int updateWorkItem(long id, WorkItem workItem,  long whoEdits) {
        Optional<WorkItem> workItemData = workItemRepository.findById(id);
        if (workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(workItem_.getCanEdit().contains(whoEdits_)){
                workItem_ = fillWorkItem(workItem_, workItem);
                workItemRepository.save(workItem_);
                bpmNparser.updateWorkItemInAllWorkflows(workItem_,true,null);
                return 1;
            }
            return 3; // cannot edit
        } else return 2;
    }

    public int addWorkItemState(long id, State state,  long whoEdits){
        Optional<WorkItem> workItemData = workItemRepository.findById(id);
        if(workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(workItem_.getCanEdit().contains(whoEdits_)) {
                state.setWorkItem(workItem_);
                stateRepository.save(state);
                return 1;
            }
            return 3;

        }
        else
        {
            return 2;
        }
    }

    public int removeWorkItemState(long id, State state, long whoEdits){
        Optional<WorkItem> workItemData = workItemRepository.findById(id);
        if(workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(workItem_.getCanEdit().contains(whoEdits_)) {
                State state_ = stateRepository.findById(state.getId()).get();
                if (state_.getWorkItem().getId() == workItem_.getId()) {
                    stateRepository.delete(state_);
                    return 1;
                } else {
                    return 4;
                }
            }
            return 3;
        }
        else
        {
            return 2;
        }
    }

    public int addRelationToWorkItem(long id, WorkItem workItem, String relationType, long whoEdits){
        if(id == workItem.getId())
            return 5;
        Optional<WorkItem> workItemData = workItemRepository.findById(id);

        if(workItemData.isPresent()){
            WorkItem workItem_ = workItemData.get();

            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(workItem_.getCanEdit().contains(whoEdits_)) {
                List<WorkItemRelation> relations = workItem_.getRelationsToAnotherWorkItems();
                for (WorkItemRelation relation : relations) {
                    if (relation.getBaseWorkItem().getId() == workItem.getId()) {
                        return 4; // already has relation
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
            return 3; //cannot edit
        }
        else
        {
            return 2;
        }
    }

    public int removeRelationFromWorkItem(long id, WorkItemRelation workItemRelation, long whoEdits){
        Optional<WorkItem> workItemData = workItemRepository.findById(id);

        if(workItemData.isPresent()){
            WorkItem workItem_ = workItemData.get();
            WorkItemRelation workItemRelation_ = workItemRelationRepository.findById(workItemRelation.getId()).get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(workItem_.getCanEdit().contains(whoEdits_)) {

                List<WorkItemRelation> relations = workItem_.getRelationsToAnotherWorkItems();
                relations.remove(workItemRelation_);
                workItem_.setRelationsToAnotherWorkItems(relations);
                workItemRepository.save(workItem_);
                workItemRelationRepository.delete(workItemRelation_);
                return 1;
            }
            return 3; //cannot edit
        }
        else
        {
            return 2;
        }
    }

    public List<WorkItem> getAllTemplates(long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            List<WorkItem> allTemplates = workItemRepository.findAllWorkItemTemplateForUser(user);
            return allTemplates;
        }
        else return null;
    }

    public int addAccess(long workItemId, long whoEdits, User getAccess){
        Optional<WorkItem> workItemData = workItemRepository.findById(workItemId);
        if(workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(workItem_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(getAccess.getId()).get();
                if(workItem_.getHasAccess().contains(getAccess_)) {
                    return 3; //already has access
                }
                if(workItem_.getCanEdit().contains(getAccess_)){
                    var list = workItem_.getCanEdit();
                    list.remove(getAccess_);
                    workItem_.setCanEdit(list);
                }
                    var list = workItem_.getHasAccess();
                    list.add(getAccess_);
                    workItem_.setHasAccess(list);
                    workItemRepository.save(workItem_);
                    return 1; //OK

            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int removeAccess(long workItemId, long whoEdits, User removeAccess){
        Optional<WorkItem> workItemData = workItemRepository.findById(workItemId);
        if(workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(workItem_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(removeAccess.getId()).get();
                if(workItem_.getHasAccess().contains(getAccess_)) {
                    var list = workItem_.getHasAccess();
                    list.remove(getAccess_);
                    workItem_.setHasAccess(list);
                    workItemRepository.save(workItem_);
                    return 1; //access removed
                } else{
                    return 3; //nothing to remove
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int removeEdit(long workItemId, long whoEdits, User removeEdit){
        Optional<WorkItem> workItemData = workItemRepository.findById(workItemId);
        if(workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(workItem_.getCanEdit().contains(whoEdits_)){
                User removeEdit_ = userRepository.findById(removeEdit.getId()).get();
                if(workItem_.getCanEdit().contains(removeEdit_)) {
                    var list = workItem_.getCanEdit();
                    list.remove(removeEdit_);
                    workItem_.setCanEdit(list);
                    workItemRepository.save(workItem_);
                    return 1; //edit removed
                } else{
                    return 3; //nothing to remove
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int addEdit(long workItemId, long whoEdits, User getEdit){
        Optional<WorkItem> workItemData = workItemRepository.findById(workItemId);
        if(workItemData.isPresent()) {
            WorkItem workItem_ = workItemData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(workItem_.getCanEdit().contains(whoEdits_)){
                User getEdit_ = userRepository.findById(getEdit.getId()).get();
                if(workItem_.getCanEdit().contains(getEdit_)){
                    return 4; //already can edit
                } else if(workItem_.getHasAccess().contains(getEdit_)) {
                    var list = workItem_.getHasAccess();
                    list.remove(getEdit_);
                    workItem_.setHasAccess(list);
                    list = workItem_.getCanEdit();
                    list.add(getEdit_);
                    workItem_.setCanEdit(list);
                    workItemRepository.save(workItem_);
                    return 1; //OK
                } else{
                    var list = workItem_.getCanEdit();
                    list.add(getEdit_);
                    workItem_.setCanEdit(list);
                    workItemRepository.save(workItem_);
                    return 1; //OK
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }
}
