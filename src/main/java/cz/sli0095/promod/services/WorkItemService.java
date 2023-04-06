package cz.sli0095.promod.services;

import cz.sli0095.promod.entities.Process;
import cz.sli0095.promod.entities.snapshots.SnapshotWorkItem;
import cz.sli0095.promod.services.configurations.ConfigurationHelper;
import cz.sli0095.promod.services.configurations.ConfigurationWorkItemService;
import cz.sli0095.promod.services.snaphsots.SnapshotWorkItemService;
import cz.sli0095.promod.services.snaphsots.SnapshotsHelper;
import cz.sli0095.promod.utils.ItemUsersUtil;
import cz.sli0095.promod.repositories.StateRepository;
import cz.sli0095.promod.repositories.WorkItemRepository;
import cz.sli0095.promod.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    ProcessService processService;
    @Autowired
    TaskService taskService;
    @Autowired
    ProjectService projectService;
    @Autowired
    ConfigurationWorkItemService configurationWorkItemService;


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
        for(Item i : workItem.getConfigurations()){
            WorkItem w = (WorkItem) i;
            w.setCreatedFrom(null);
            workItemRepository.save(w);
        }
        workItemRepository.deleteById(id);
        return true;
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
    }

    public int removeWorkItemState(long id, State state, long whoEdits) {
        WorkItem workItem = getWorkItemById(id);
        if (workItem == null) {
            return 2;
        }
        User editor = userService.getUserById(whoEdits);
        if (editor == null || !ItemUsersUtil.getAllUsersCanEdit(workItem).contains(editor)) {
            return 3;
        }
        state = stateService.getStateById(state.getId());
        if (state == null) {
            return 4;
        }
        if (state.getWorkItem().getId() != workItem.getId()) {
            return 4;
        }
        stateRepository.delete(state);
        return 1;
    }

    public List<WorkItem> getAllUserCanView(long userId, Long projectId) {
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return workItemRepository.findAllCanUserViewInDefault(user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return workItemRepository.findAllCanUserView(user, project);
    }

    public List<WorkItem> getAllUserCanEdit(long userId, Long projectId) {
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return workItemRepository.findAllCanUserEditInDefault(user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return workItemRepository.findAllCanUserEdit(user, project);
    }

    public List<WorkItem> getAllUserCanViewByTemplate(long userId, boolean isTemplate, Long projectId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        if(projectId == -1){
            return workItemRepository.findByIsTemplateUserCanViewInDefault(isTemplate, user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return workItemRepository.findByIsTemplateUserCanView(isTemplate,user, project);
    }

    public List<WorkItem> getUsableInProcessForUser(long userId, long processId, Long projectId){
        User user = userService.getUserById(userId);
        if (user == null || !processService.processExists(processId)) {
            return new ArrayList<>();
        }
        if(projectId == -1){
            return workItemRepository.findUsableInProcessForUserCanEditInDefault(processId, user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return workItemRepository.findUsableInProcessForUserCanEdit(processId, user, project);
    }

    public List<WorkItem> getUsableInTaskForUser(long userId, long taskId, Long projectId){
        User user = userService.getUserById(userId);
        if (user == null || !taskService.taskExists(taskId)) {
            return new ArrayList<>();
        }
        if(projectId == -1){
            return workItemRepository.findUsableInTaskForUserCanEditInDefault(taskId, user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return workItemRepository.findUsableInTaskForUserCanEdit(taskId, user , project);
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
        } else if (element instanceof cz.sli0095.promod.entities.Process) {
            var list =  workItem.getCanBeUsedInProcesses();
            if(list.contains(element)){
                return 3;
            }
            list.add((cz.sli0095.promod.entities.Process) element);
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
        } else if (element instanceof cz.sli0095.promod.entities.Process) {
            var list =  workItem.getCanBeUsedInProcesses();
            if(!list.contains(element)){
                return 3;
            }
            list.remove((cz.sli0095.promod.entities.Process) element);
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

    public WorkItem revertWorkItem(long userId, SnapshotWorkItem snapshot) {
        snapshot = snapshotWorkItemService.getSnapshotWorkItemById(snapshot.getId());
        if(snapshot == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        WorkItem workItem = getWorkItemById(snapshot.getOriginalId());
        if(workItem == null){
            return null;
        }
        if(!ItemUsersUtil.getAllUsersCanEdit(workItem).contains(user)){
            return null;
        }
        return snapshotWorkItemService.revertFromSnapshot(snapshot,new SnapshotsHelper());
    }

    public List<Process> getUsableInProcesses(Long id) {
        WorkItem workItem = getWorkItemById(id);
        if(workItem == null){
            return null;
        }
        return workItem.getCanBeUsedInProcesses();
    }

    public List<Task> getUsableInTasks(Long id) {
        WorkItem workItem = getWorkItemById(id);
        if(workItem == null){
            return null;
        }
        return workItem.getCanBeUsedIn();
    }

    public void deleteAllStates(long id){
        WorkItem workItem = getWorkItemById(id);
        for(State state : workItem.getWorkItemStates()){
            stateRepository.deleteById(state.getId());
        }
    }

    public WorkItem createNewConfiguration(long userId, long workItemId, long projectId) {
        WorkItem workItem = getWorkItemById(workItemId);
        if(workItem == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        if(projectId == -1){
            return configurationWorkItemService.createNewConfiguration(workItem, new ConfigurationHelper(),  user, null);
        }
        Project project = projectService.getProjectById(projectId);
        if(project != null && ItemUsersUtil.getAllUsersCanEdit(project).contains(user)){
            return configurationWorkItemService.createNewConfiguration(workItem, new ConfigurationHelper(), user, project);
        }
        return null;
    }

    public int changeOwner(long id, long editorId, long newOwnerId){
        WorkItem workItem = getWorkItemById(id);
        if(workItem == null){
            return 2;
        }
        User editor = userService.getUserById(editorId);
        if(editor == null){
            return 3;
        }
        User newOwner = userService.getUserById(newOwnerId);
        if(newOwner == null){
            return 3;
        }
        if(workItem.getOwner().getId() != editor.getId()){
            return 4; //MUST BE OWNER TO CHANGE OWNER
        }
        workItem.setOwner(newOwner);
        workItemRepository.save(workItem);
        return 1;
    }
}
