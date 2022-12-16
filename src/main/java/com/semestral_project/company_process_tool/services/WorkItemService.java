package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotWorkItem;
import com.semestral_project.company_process_tool.repositories.StateRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import com.semestral_project.company_process_tool.repositories.WorkItemRepository;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotWorkItemService;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotsHelper;
import com.semestral_project.company_process_tool.utils.ItemUsersUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class WorkItemService {

    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    StateRepository stateRepository;
    @Autowired
    StateService stateService;
    @Autowired
    BPMNparser bpmNparser;
    @Autowired
    UserService userService;
    @Autowired
    SnapshotWorkItemService snapshotWorkItemService;
    @Autowired
    UserTypeService userTypeService;
    @Autowired
    ElementService elementService;



    public WorkItem fillWorkItem(WorkItem oldWorkItem, WorkItem updatedWorkItem) {
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

    public List<WorkItem> getAllWorkItems() {
        try {
            return (List<WorkItem>) workItemRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public WorkItem getWorkItemById(long id) {
        Optional<WorkItem> workItemData = workItemRepository.findById(id);
        return workItemData.orElse(null);
    }

    public long addWorkItem(WorkItem workItem, long userId) {
        User owner = userService.getUserById(userId);
        if(owner == null){
            return -1;
        }
        workItem.setOwner(owner);
        workItem = workItemRepository.save(workItem);
        return workItem.getId();
    }

    public boolean deleteWorkItem(long id, long whoEdits) {
       WorkItem workItem = getWorkItemById(id);
       if(workItem == null){
           return false;
       }
       User editor = userService.getUserById(whoEdits);
       if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)){
           return false;
       }
       if(!bpmNparser.removeWorkItemFromAllWorkflows(workItem)){
           return false;
       }
        for(SnapshotWorkItem snapshot : workItem.getSnapshots()){
            snapshot.setOriginalWorkItem(null);
        }
        workItemRepository.deleteById(id);
        return true;
//
//
//        try {
//            Optional<WorkItem> workItemData = workItemRepository.findById(id);
//            if (workItemData.isPresent()) {
//                WorkItem workItem_ = workItemData.get();
//                User whoEdits_ = userRepository.findById(whoEdits).get();
//                if (workItem_.getCanEdit().contains(whoEdits_)) {
//                    if (bpmNparser.removeWorkItemFromAllWorkflows(workItemRepository.findById(id).get())) {
//                        for(SnapshotWorkItem snapshot : workItem_.getSnapshots()){
//                            snapshot.setOriginalWorkItem(null);
//                        }
//                        workItemRepository.deleteById(id);
//                        return true;
//                    }
//                }
//            }
//            return false;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return false;
//        }
    }

    public int updateWorkItem(long id, WorkItem workItem, long whoEdits) {
        WorkItem workItemMain = getWorkItemById(id);
        if(workItemMain == null){
            return 2;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItemMain).contains(editor)){
            return 3;
        }
        workItemMain = fillWorkItem(workItemMain, workItem);
        workItemMain = workItemRepository.save(workItemMain);
        bpmNparser.updateWorkItemInAllWorkflows(workItemMain, true, null);
        return 1;
    }

    public int updateIsTemplate(long id, boolean isTemplate, long whoEdits) {
        WorkItem workItemMain = getWorkItemById(id);
        if (workItemMain == null) {
            return 2;
        }
        User editor = userService.getUserById(whoEdits);
        if (editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItemMain).contains(editor)) {
            return 3;
        }
        workItemMain.setTemplate(isTemplate);
        workItemRepository.save(workItemMain);
        return 1;
    }

    public int addWorkItemState(long id, State state, long whoEdits) {
        WorkItem workItem = getWorkItemById(id);
        if(workItem == null){
            return 2;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)){
            return 3;
        }
        state.setWorkItem(workItem);
        stateRepository.save(state);
        return 1;


//        Optional<WorkItem> workItemData = workItemRepository.findById(id);
//        if (workItemData.isPresent()) {
//            WorkItem workItem_ = workItemData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if (workItem_.getCanEdit().contains(whoEdits_)) {
//                state.setWorkItem(workItem_);
//                stateRepository.save(state);
//                return 1;
//            }
//            return 3;
//
//        } else {
//            return 2;
//        }
    }

    public int removeWorkItemState(long id, State state, long whoEdits) {
        WorkItem workItem = getWorkItemById(id);
        if(workItem == null){
            return 2;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)){
            return 3;
        }
        state = stateService.getStateById(state.getId());
        if(state == null){
            return 4;
        }
        if(state.getWorkItem().getId() != workItem.getId()){
            return 4;
        }
        stateRepository.delete(state);
        return 1;


//        Optional<WorkItem> workItemData = workItemRepository.findById(id);
//        if (workItemData.isPresent()) {
//            WorkItem workItem_ = workItemData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if (workItem_.getCanEdit().contains(whoEdits_)) {
//                State state_ = stateRepository.findById(state.getId()).get();
//                if (state_.getWorkItem().getId() == workItem_.getId()) {
//                    stateRepository.delete(state_);
//                    return 1;
//                } else {
//                    return 4;
//                }
//            }
//            return 3;
//        } else {
//            return 2;
//        }
    }

//    public int addRelationToWorkItem(long id, WorkItem workItem, String relationType, long whoEdits){
//        if(id == workItem.getId())
//            return 5;
//        Optional<WorkItem> workItemData = workItemRepository.findById(id);
//
//        if(workItemData.isPresent()){
//            WorkItem workItem_ = workItemData.get();
//
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(workItem_.getCanEdit().contains(whoEdits_)) {
//                List<WorkItemRelation> relations = workItem_.getRelationsToAnotherWorkItems();
//                for (WorkItemRelation relation : relations) {
//                    if (relation.getBaseWorkItem().getId() == workItem.getId()) {
//                        return 4; // already has relation
//                    }
//                }
//                WorkItemRelation relation = new WorkItemRelation();
//                relation.setBaseWorkItem(workItem);
//                relation.setRelatedWorkItem(workItem_);
//                relation.setRelationType(relationType);
//                relation = workItemRelationRepository.save(relation);
//                relations.add(relation);
//                workItem_.setRelationsToAnotherWorkItems(relations);
//                workItemRepository.save(workItem_);
//                return 1;
//            }
//            return 3; //cannot edit
//        }
//        else
//        {
//            return 2;
//        }
//    }
//
//    public int removeRelationFromWorkItem(long id, WorkItemRelation workItemRelation, long whoEdits){
//        Optional<WorkItem> workItemData = workItemRepository.findById(id);
//
//        if(workItemData.isPresent()){
//            WorkItem workItem_ = workItemData.get();
//            WorkItemRelation workItemRelation_ = workItemRelationRepository.findById(workItemRelation.getId()).get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(workItem_.getCanEdit().contains(whoEdits_)) {
//
//                List<WorkItemRelation> relations = workItem_.getRelationsToAnotherWorkItems();
//                relations.remove(workItemRelation_);
//                workItem_.setRelationsToAnotherWorkItems(relations);
//                workItemRepository.save(workItem_);
//                workItemRelationRepository.delete(workItemRelation_);
//                return 1;
//            }
//            return 3; //cannot edit
//        }
//        else
//        {
//            return 2;
//        }
//    }

    public List<WorkItem> getAllUserCanView(long userId) {
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<WorkItem> ret = new HashSet<>();
        List<WorkItem> workItems = (List<WorkItem>) workItemRepository.findAll();
        for(WorkItem w : workItems){
            if(ItemUsersUtil.getAllUsersCanView(w).contains(user)){
                ret.add(w);
            }
        }
        return new ArrayList<>(ret);


//        if (userRepository.existsById(userId)) {
//            User user = userRepository.findById(userId).get();
//            return workItemRepository.findAllWorkItemTemplateForUser(user);
//        } else return null;
    }

    public List<WorkItem> getAllUserCanEdit(long userId) {
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<WorkItem> ret = new HashSet<>();
        List<WorkItem> workItems = (List<WorkItem>) workItemRepository.findAll();
        for(WorkItem w : workItems){
            if(ItemUsersUtil.getAllCanEdit(w).contains(user)){
                ret.add(w);
            }
        }
        return new ArrayList<>(ret);
    }

    public List<WorkItem> getAllUserCanViewByTemplate(long userId, boolean isTemplate) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        HashSet<WorkItem> ret = new HashSet<>();
        List<WorkItem> workItems = workItemRepository.findByIsTemplate(isTemplate);
        for (WorkItem w : workItems) {
            if (ItemUsersUtil.getAllUsersCanView(w).contains(user)) {
                ret.add(w);
            }
        }
        return new ArrayList<>(ret);
    }

    public List<WorkItem> getUsableInProcessForUser(long userId, Process process){
        User user = userService.getUserById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        HashSet<WorkItem> ret = new HashSet<>();
        List<WorkItem> workItems = workItemRepository.usableInProcessForUser(process);
        for (WorkItem w : workItems) {
            if (ItemUsersUtil.getAllUsersCanView(w).contains(user)) {
                ret.add(w);
            }
        }
        return new ArrayList<>(ret);
    }

    public List<WorkItem> getUsableInTaskForUser(long userId, Task task){
        User user = userService.getUserById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        HashSet<WorkItem> ret = new HashSet<>();
        List<WorkItem> workItems = workItemRepository.usableInTaskForUser(task);
        for (WorkItem w : workItems) {
            if (ItemUsersUtil.getAllUsersCanView(w).contains(user)) {
                ret.add(w);
            }
        }
        return new ArrayList<>(ret);
    }

    public int addAccess(long workItemId, long whoEdits, UserType getAccess) {
        WorkItem workItem = getWorkItemById(workItemId);
        if(workItem == null){
            return 2; //workItem not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(getAccess.getId());
        if(access == null){
            return 5;
        }
        if(workItem.getHasAccess().contains(access) || workItem.getOwner() == access){
            return 3; //already has access
        }
        var list = workItem.getCanEdit();
        if(list.contains(access)){
            list.remove(access);
            workItem.setCanEdit(list);
        }
        list = workItem.getHasAccess();
        list.add(access);
        workItem.setHasAccess(list);
        workItemRepository.save(workItem);
        return  1; //OK


//        Optional<WorkItem> workItemData = workItemRepository.findById(workItemId);
//        if (workItemData.isPresent()) {
//            WorkItem workItem_ = workItemData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if (workItem_.getCanEdit().contains(whoEdits_)) {
//                User getAccess_ = userRepository.findById(getAccess.getId()).get();
//                if (workItem_.getHasAccess().contains(getAccess_)) {
//                    return 3; //already has access
//                }
//                if (workItem_.getCanEdit().contains(getAccess_)) {
//                    var list = workItem_.getCanEdit();
//                    if (list.size() == 1) {
//                        return 6;
//                    }
//                    list.remove(getAccess_);
//                    workItem_.setCanEdit(list);
//                }
//                var list = workItem_.getHasAccess();
//                list.add(getAccess_);
//                workItem_.setHasAccess(list);
//                workItemRepository.save(workItem_);
//                return 1; //OK
//
//            } else return 5; //cannot edit
//        } else {
//            return 2; //role not found
//        }
    }

    public int removeAccess(long workItemId, long whoEdits, UserType removeAccess) {
        WorkItem workItem = getWorkItemById(whoEdits);
        if(workItem == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(removeAccess.getId());
        if(access == null){
            return 5;
        }
        if(!workItem.getHasAccess().contains(access)){
            return 3; //nothing to remove
        }
        var list = workItem.getHasAccess();
        list.remove(access);
        workItem.setHasAccess(list);
        workItemRepository.save(workItem);
        return  1; //OK


//        Optional<WorkItem> workItemData = workItemRepository.findById(workItemId);
//        if (workItemData.isPresent()) {
//            WorkItem workItem_ = workItemData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if (workItem_.getCanEdit().contains(whoEdits_)) {
//                User getAccess_ = userRepository.findById(removeAccess.getId()).get();
//                if (workItem_.getHasAccess().contains(getAccess_)) {
//                    var list = workItem_.getHasAccess();
//                    list.remove(getAccess_);
//                    workItem_.setHasAccess(list);
//                    workItemRepository.save(workItem_);
//                    return 1; //access removed
//                } else {
//                    return 3; //nothing to remove
//                }
//            } else return 5; //cannot edit
//        } else {
//            return 2; //workItem not found
//        }
    }

    public int removeEdit(long workItemId, long whoEdits, UserType removeEdit) {
        WorkItem workItem = getWorkItemById(workItemId);
        if(workItem == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(removeEdit.getId());
        if(edit == null){
            return 5;
        }
        if(!workItem.getCanEdit().contains(edit)){
            return 3; //nothing to remove
        }
        var list = workItem.getCanEdit();
        list.remove(edit);
        workItem.setCanEdit(list);
        workItemRepository.save(workItem);
        return  1; //OK


//        Optional<WorkItem> workItemData = workItemRepository.findById(workItemId);
//        if (workItemData.isPresent()) {
//            WorkItem workItem_ = workItemData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if (workItem_.getCanEdit().contains(whoEdits_)) {
//                User removeEdit_ = userRepository.findById(removeEdit.getId()).get();
//                if (workItem_.getCanEdit().contains(removeEdit_)) {
//                    var list = workItem_.getCanEdit();
//                    if (list.size() == 1) {
//                        return 6;
//                    }
//                    list.remove(removeEdit_);
//                    workItem_.setCanEdit(list);
//                    workItemRepository.save(workItem_);
//                    return 1; //edit removed
//                } else {
//                    return 3; //nothing to remove
//                }
//            } else return 5; //cannot edit
//        } else {
//            return 2; //role not found
//        }
    }

    public int addEdit(long workItemId, long whoEdits, UserType getEdit) {
        WorkItem workItem = getWorkItemById(workItemId);
        if(workItem == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(getEdit.getId());
        if(edit == null){
            return 5;
        }
        if(workItem.getCanEdit().contains(edit) || workItem.getOwner() == edit){
            return 4; //already can edit
        }
        var list = workItem.getHasAccess();
        if(list.contains(edit)){
            list.remove(edit);
            workItem.setHasAccess(list);
        }
        list = workItem.getCanEdit();
        list.add(edit);
        workItem.setCanEdit(list);
        workItemRepository.save(workItem);
        return  1; //OK


//        Optional<WorkItem> workItemData = workItemRepository.findById(workItemId);
//        if (workItemData.isPresent()) {
//            WorkItem workItem_ = workItemData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if (workItem_.getCanEdit().contains(whoEdits_)) {
//                User getEdit_ = userRepository.findById(getEdit.getId()).get();
//                if (workItem_.getCanEdit().contains(getEdit_)) {
//                    return 4; //already can edit
//                } else if (workItem_.getHasAccess().contains(getEdit_)) {
//                    var list = workItem_.getHasAccess();
//                    list.remove(getEdit_);
//                    workItem_.setHasAccess(list);
//                    list = workItem_.getCanEdit();
//                    list.add(getEdit_);
//                    workItem_.setCanEdit(list);
//                    workItemRepository.save(workItem_);
//                    return 1; //OK
//                } else {
//                    var list = workItem_.getCanEdit();
//                    list.add(getEdit_);
//                    workItem_.setCanEdit(list);
//                    workItemRepository.save(workItem_);
//                    return 1; //OK
//                }
//            } else return 5; //cannot edit
//        } else {
//            return 2; //role not found
//        }
    }

    public int addUsableIn(long workItemId, long user,  Element element) {
        WorkItem workItem = getWorkItemById(workItemId);
        if(workItem == null){
            return 2; //workItem not found
        }
        User editor = userService.getUserById(user);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)){
            return 5; //cannot edit
        }
        element = elementService.getElementById(element.getId());
        if(!ItemUsersUtil.getAllUsersCanEdit(element).contains(editor)){
            return 5;
        }
        if(element instanceof Task){
           var list =  workItem.getCanBeUsedIn();
           if(list.contains(element)){
               return 3;
           }
           list.add((Task) element);
           workItem.setCanBeUsedIn(list);
           workItemRepository.save(workItem);
           return 1;
        } else if (element instanceof Process) {
            var list =  workItem.getCanBeUsedInProcesses();
            if(list.contains(element)){
                return 3;
            }
            list.add((Process) element);
            workItem.setCanBeUsedInProcesses(list);
            workItemRepository.save(workItem);
            return 1; //OK
        }
        return  5;
    }

    public int removeUsableIn(long workItemId, long user,  Element element) {
        WorkItem workItem = getWorkItemById(workItemId);
        if(workItem == null){
            return 2; //workItem not found
        }
        User editor = userService.getUserById(user);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)){
            return 5; //cannot edit
        }
        element = elementService.getElementById(element.getId());
        if(!ItemUsersUtil.getAllUsersCanEdit(element).contains(editor)){
            return 5;
        }
        if(element instanceof Task){
            var list =  workItem.getCanBeUsedIn();
            if(!list.contains(element)){
                return 3;
            }
            list.remove((Task) element);
            workItem.setCanBeUsedIn(list);
            workItemRepository.save(workItem);
            return 1;
        } else if (element instanceof Process) {
            var list =  workItem.getCanBeUsedInProcesses();
            if(!list.contains(element)){
                return 3;
            }
            list.remove((Process) element);
            workItem.setCanBeUsedInProcesses(list);
            workItemRepository.save(workItem);
            return 1; //OK
        }
        return  5;
    }

    public int createSnapshot(Long id, long userId, String description) {
        WorkItem workItem = getWorkItemById(id);
        if(workItem == null){
            return 2;
        }
        User editor = userService.getUserById(userId);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)) {
            return 3;
        }
        snapshotWorkItemService.createSnapshot(workItem, description, new SnapshotsHelper());
        return 1;
    }

    public WorkItem restoreWorkItem(long userId, SnapshotWorkItem snapshot) {
        snapshot = snapshotWorkItemService.getSnapshotWorkItemById(snapshot.getId());
        if(snapshot == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        return snapshotWorkItemService.restoreFromSnapshot(snapshot,new SnapshotsHelper(), user);
    }
}
