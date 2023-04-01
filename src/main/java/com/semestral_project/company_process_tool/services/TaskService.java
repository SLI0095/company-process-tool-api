package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotElement;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotTask;
import com.semestral_project.company_process_tool.repositories.*;
import com.semestral_project.company_process_tool.services.configurations.ConfigurationHelper;
import com.semestral_project.company_process_tool.services.configurations.ConfigurationTaskService;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotTaskService;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotsHelper;
import com.semestral_project.company_process_tool.utils.BPMNSnapshotUtil;
import com.semestral_project.company_process_tool.utils.ItemUsersUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskStepRepository taskStepRepository;
    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    RasciRepository rasciRepository;
    @Autowired
    BPMNparser bpmNparser;
    @Autowired
    SnapshotTaskService snapshotTaskService;
    @Autowired
    UserTypeService userTypeService;
    @Autowired
    UserService userService;
    @Autowired
    TaskStepService taskStepService;
    @Autowired
    WorkItemService workItemService;
    @Autowired
    ProcessService processService;
    @Autowired
    ProjectService projectService;
    @Autowired
    ConfigurationTaskService configurationTaskService;

    public Task fillTask(Task oldTask, Task updatedTask){
        oldTask.setName(updatedTask.getName());
        oldTask.setBriefDescription(updatedTask.getBriefDescription());
        oldTask.setMainDescription(updatedTask.getMainDescription());
        oldTask.setVersion(updatedTask.getVersion());
        oldTask.setChangeDate(updatedTask.getChangeDate());
        oldTask.setChangeDescription(updatedTask.getChangeDescription());
        oldTask.setPurpose(updatedTask.getPurpose());
        oldTask.setKeyConsiderations(updatedTask.getKeyConsiderations());
        oldTask.setTaskType(updatedTask.getTaskType());
        return oldTask;
    }

    public List<Task> getAllTasks(){
        try {
            return (List<Task>) taskRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean taskExists(long id){
        return taskRepository.existsById(id);
    }

    public Task getTaskById(long id){
        Optional<Task> taskData = taskRepository.findById(id);
        return taskData.orElse(null);
    }

    public long addTask(Task task, long userId) {
        User owner = userService.getUserById(userId);
        if(owner == null){
            return -1;
        }
        task.setOwner(owner);
        task = taskRepository.save(task);
        return task.getId();
    }

    public List<Process> getUsableIn(long id){
        Task task = getTaskById(id);
        if(task == null){
            return null;
        }
        return task.getCanBeUsedIn();
    }

    public int addAccess(long taskId, long whoEdits, UserType getAccess){
        Task task = getTaskById(taskId);
        if(task == null){
            return 2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(getAccess.getId());
        if(access == null){
            return 5;
        }
        if(task.getHasAccess().contains(access) || task.getOwner() == access){
            return 3; //already has access
        }
        var list = task.getCanEdit();
        if(list.contains(access)){
            list.remove(access);
            task.setCanEdit(list);
        }
        list = task.getHasAccess();
        list.add(access);
        task.setHasAccess(list);
        taskRepository.save(task);
        return  1; //OK
    }


    public void addAccessAutomatic(long taskId, UserType getAccess){
        Task task = getTaskById(taskId);
        if(task == null){
            return; //task not found
        }
        UserType access = userTypeService.getUserTypeById(getAccess.getId());
        if(access == null){
            return;
        }
        if(task.getHasAccess().contains(access) || task.getOwner() == access){
            return; //already has access
        }
        var list = task.getCanEdit();
        if(list.contains(access)){
            list.remove(access);
            task.setCanEdit(list);
        }
        list = task.getHasAccess();
        list.add(access);
        task.setHasAccess(list);
        taskRepository.save(task);
    }

    public int removeAccess(long taskId, long whoEdits, UserType removeAccess){
        Task task = getTaskById(taskId);
        if(task == null){
            return 2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(removeAccess.getId());
        if(access == null){
            return 5;
        }
        if(!task.getHasAccess().contains(access)){
            return 3; //nothing to remove
        }
        var list = task.getHasAccess();
        list.remove(access);
        task.setHasAccess(list);
        taskRepository.save(task);
        return  1; //OK
    }

    public int removeEdit(long taskId, long whoEdits, UserType removeEdit){
        Task task = getTaskById(taskId);
        if(task == null){
            return 2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(removeEdit.getId());
        if(edit == null){
            return 5;
        }
        if(!task.getCanEdit().contains(edit)){
            return 3; //nothing to remove
        }
        var list = task.getCanEdit();
        list.remove(edit);
        task.setCanEdit(list);
        taskRepository.save(task);
        return  1; //OK
    }

    public int addEdit(long taskId, long whoEdits, UserType getEdit){
        Task task = getTaskById(taskId);
        if(task == null){
            return 2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(getEdit.getId());
        if(edit == null){
            return 5;
        }
        if(task.getCanEdit().contains(edit) || task.getOwner() == edit){
            return 4; //already can edit
        }
        var list = task.getHasAccess();
        if(list.contains(edit)){
            list.remove(edit);
            task.setHasAccess(list);
        }
        list = task.getCanEdit();
        list.add(edit);
        task.setCanEdit(list);
        taskRepository.save(task);
        return  1; //OK
    }

    public void addEditAutomatic(long taskId, UserType getEdit){
        Task task = getTaskById(taskId);
        if(task == null){
            return; //task not found
        }
        UserType edit = userTypeService.getUserTypeById(getEdit.getId());
        if(edit == null){
            return;
        }
        if(task.getCanEdit().contains(edit) || task.getOwner() == edit){
            return; //already can edit
        }
        var list = task.getHasAccess();
        if(list.contains(edit)){
            list.remove(edit);
            task.setHasAccess(list);
        }
        list = task.getCanEdit();
        list.add(edit);
        task.setCanEdit(list);
        taskRepository.save(task);
    }


    public int updateTask(long id, Task task, long whoEdits){
        Task mainTask = getTaskById(id);
        if (mainTask == null){
            return  2;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainTask).contains(editor)){
            return 3;
        }
        mainTask = fillTask(mainTask, task);
        bpmNparser.updateTaskInAllWorkflows(mainTask, true, false, mainTask.getTaskType(), null);
        taskRepository.save(mainTask);
        return 1;
    }

    public int updateIsTemplate(long id, boolean isTemplate, long whoEdits) {
        Task mainTask = getTaskById(id);
        if (mainTask == null) {
            return 2;
        }
        User editor = userService.getUserById(whoEdits);
        if (editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainTask).contains(editor)) {
            return 3;
        }
        mainTask.setTemplate(isTemplate);
        taskRepository.save(mainTask);
        return 1;
    }

    public int removeTaskById(long id, long whoEdits){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 3; //cannot edit
        }
        if (!bpmNparser.removeTaskFromAllWorkflows(task)) {
            return 3;
        }
        var list = task.getMandatoryInputs();
        for (WorkItem w : list) {
            var list2 = w.getAsMandatoryInput();
            list2.remove(task);
            w.setAsMandatoryInput(list2);
            workItemRepository.save(w);
        }
        list = task.getOutputs();
        for (WorkItem w : list) {
            var list2 = w.getAsOutput();
            list2.remove(task);
            w.setAsOutput(list2);
            workItemRepository.save(w);
        }
        for(SnapshotElement snapshot : task.getSnapshots()){
            snapshot.setOriginalElement(null);
        }
        taskRepository.deleteById(id);
        return 1;
    }

    public int addTaskStep(long id, TaskStep taskStep, long whoEdits){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 3; //cannot edit
        }
        taskStep.setTask(task);
        taskStepRepository.save(taskStep);
        return 1;
    }

    public int removeTaskStep(long id, TaskStep taskStep, long whoEdits){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 3; //cannot edit
        }
        taskStep = taskStepService.getTaskStepById(taskStep.getId());
        if(taskStep == null){
            return 3;
        }
        if(taskStep.getTask().getId() != task.getId()){
            return 4;
        }
        taskStepRepository.delete(taskStep);
        return 1;
    }


    public int addRasci(long id, Rasci rasci, long whoEdits){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 3; //cannot edit
        }
        List<Rasci> rasciList = task.getRasciList();
        for (Rasci r : rasciList) {
            if (r.getRole().getId() == rasci.getRole().getId())
                return 4; //role already in RASCI
        }
        rasci.setTask(task);
        rasciRepository.save(rasci);
        return 1;
    }

    public int removeRasci(long id, Rasci rasci, long whoEdits) {
        Task task = getTaskById(id);
        if (task == null) {
            return 2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if (editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)) {
            return 3; //cannot edit
        }
        rasciRepository.deleteById(rasci.getId());
        return 1;
    }

    public int addMandatoryInput(long id, WorkItem workItem, long whoEdits) {
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        WorkItem item = workItemService.getWorkItemById(workItem.getId());
        if(item == null){
            return 3;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 3; //cannot edit
        }
        if (task.getMandatoryInputs().contains(item)) {
            return 4; // already in inputs
        }
        List<Task> tasksList = item.getAsMandatoryInput();
        tasksList.add(task);
        item.setAsMandatoryInput(tasksList);
        workItemRepository.save(item);
        return 1;
    }

    public void addMandatoryInputWithoutUser(long id, WorkItem workItem) {
        Task task = getTaskById(id);
        if (task == null){
            return; //task not found
        }
        WorkItem item = workItemService.getWorkItemById(workItem.getId());
        if(item == null){
            return;
        }
        List<WorkItem> inputList = task.getMandatoryInputs();
        if (task.getMandatoryInputs().contains(item)) {
            return; // already in inputs
        }
        List<Task> tasksList = item.getAsMandatoryInput();
        tasksList.add(task);
        item.setAsMandatoryInput(tasksList);

        var usableList = workItem.getCanBeUsedIn();
        if(!usableList.contains(task)){
            usableList.add(task);
            workItem.setCanBeUsedIn(usableList);
        }

        workItemRepository.save(item);
    }

    public int removeMandatoryInput(long id, WorkItem workItem, long whoEdits){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        WorkItem item = workItemService.getWorkItemById(workItem.getId());
        if(item == null){
            return 3;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 3; //cannot edit
        }
        if (!task.getMandatoryInputs().contains(item)) {
            return 4; // not in inputs
        }
        bpmNparser.removeInputConnectionFromAllWorkflows(task, item);
        List<Task> tasksList = item.getAsMandatoryInput();
        tasksList.remove(task);
        item.setAsMandatoryInput(tasksList);
        workItemRepository.save(item);
        return 1;
    }

    public int removeMandatoryInputWithoutUser(long id, WorkItem workItem){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        WorkItem item = workItemService.getWorkItemById(workItem.getId());
        if(item == null){
            return 3;
        }
        if (!task.getMandatoryInputs().contains(item)) {
            return 4; // not in inputs
        }
        bpmNparser.removeInputConnectionFromAllWorkflows(task, item);
        List<Task> tasksList = item.getAsMandatoryInput();
        tasksList.remove(task);
        item.setAsMandatoryInput(tasksList);
        workItemRepository.save(item);
        return 1;

    }

    public int addOutput(long id, WorkItem workItem, long whoEdits){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        WorkItem item = workItemService.getWorkItemById(workItem.getId());
        if(item == null){
            return 3;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 3; //cannot edit
        }
        if (task.getOutputs().contains(item)) {
            return 4; // already in inputs
        }
        List<Task> tasksList = item.getAsOutput();
        tasksList.add(task);
        item.setAsOutput(tasksList);
        workItemRepository.save(item);
        return 1;
    }

    public void addOutputWithoutUser(long id, WorkItem workItem){
        Task task = getTaskById(id);
        if (task == null){
            return; //task not found
        }
        WorkItem item = workItemService.getWorkItemById(workItem.getId());
        if(item == null){
            return;
        }
        if (task.getOutputs().contains(item)) {
            return; // already in inputs
        }
        List<Task> tasksList = item.getAsOutput();
        tasksList.add(task);
        item.setAsOutput(tasksList);

        var usableList = workItem.getCanBeUsedIn();
        if(!usableList.contains(task)){
            usableList.add(task);
            workItem.setCanBeUsedIn(usableList);
        }

        workItemRepository.save(item);
    }

    public int removeOutput(long id, WorkItem workItem, long whoEdits){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        WorkItem item = workItemService.getWorkItemById(workItem.getId());
        if(item == null){
            return 3;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 3; //cannot edit
        }
        if (!task.getOutputs().contains(item)) {
            return 4; // not in inputs
        }
        bpmNparser.removeOutputConnectionFromAllWorkflows(task, item);
        List<Task> tasksList = item.getAsOutput();
        tasksList.remove(task);
        item.setAsOutput(tasksList);
        workItemRepository.save(item);
        return 1;
    }
    public int removeOutputWithoutUser(long id, WorkItem workItem){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        WorkItem item = workItemService.getWorkItemById(workItem.getId());
        if(item == null){
            return 3;
        }
        if (!task.getOutputs().contains(item)) {
            return 4; // not in inputs
        }
        bpmNparser.removeOutputConnectionFromAllWorkflows(task, item);
        List<Task> tasksList = item.getAsOutput();
        tasksList.remove(task);
        item.setAsOutput(tasksList);
        workItemRepository.save(item);
        return 1;
    }


    public List<Task> getAllUserCanView(long userId, Long projectId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return taskRepository.findAllCanUserViewInDefault(user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return taskRepository.findAllCanUserView(user, project);

    }

    public List<Task> getAllUserCanEdit(long userId, Long projectId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return taskRepository.findAllCanUserEditInDefault(user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return taskRepository.findAllCanUserEdit(user, project);
    }

    public List<Task> getAllUserCanViewFiltered(long userId, boolean isTemplate, Long projectId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        if(projectId == -1){
            return taskRepository.findByIsTemplateUserCanViewInDefault(isTemplate,user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return taskRepository.findByIsTemplateUserCanView(isTemplate,user, project);
    }

    public int addUsableIn(long taskId, long user,  Process process) {
        Task task = getTaskById(taskId);
        if(task == null){
            return 2; //task not found
        }
        User editor = userService.getUserById(user);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 5; //cannot edit
        }
        process = processService.getProcessById(process.getId());
        if(!ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5;
        }
        var list =  task.getCanBeUsedIn();
        if(list.contains(process)){
            return 3;
        }
        list.add(process);
        task.setCanBeUsedIn(list);
        taskRepository.save(task);
        return 1;
    }

    public int removeUsableIn(long taskId, long user,  Process process) {
        Task task = getTaskById(taskId);
        if(task == null){
            return 2; //task not found
        }
        User editor = userService.getUserById(user);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 5; //cannot edit
        }
        process = processService.getProcessById(process.getId());
        if(!ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5;
        }
        var list =  task.getCanBeUsedIn();
        if(!list.contains(process)){
            return 3;
        }
        list.remove(process);
        task.setCanBeUsedIn(list);
        taskRepository.save(task);
        return 1;
    }


    public int createSnapshot(Long id, long userId, String description) {
        Task task = getTaskById(id);
        if(task == null){
            return 2;
        }
        User editor = userService.getUserById(userId);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)) {
            return 3;
        }
        snapshotTaskService.createSnapshot(task, description, new SnapshotsHelper());
        return 1;
    }

    public Task restoreTask(long userId, SnapshotTask snapshot) {
        snapshot = snapshotTaskService.getSnapshotTaskById(snapshot.getId());
        if(snapshot == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        return snapshotTaskService.restoreFromSnapshot(snapshot,new SnapshotsHelper(), null, user);
    }

    public Task revertTask(long userId, SnapshotTask snapshot) {
        snapshot = snapshotTaskService.getSnapshotTaskById(snapshot.getId());
        if(snapshot == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        Task task = getTaskById(snapshot.getOriginalId());
        if(task == null){
            return null;
        }
        if(!ItemUsersUtil.getAllUsersCanEdit(task).contains(user)){
            return null;
        }
        return snapshotTaskService.revertExistingFromSnapshot(snapshot,new SnapshotsHelper(), new BPMNSnapshotUtil(null), user);
    }

    public void deleteAllSteps(long id){
        Task task = getTaskById(id);
        for(TaskStep step : task.getSteps()){
            taskStepRepository.deleteById(step.getId());
        }
    }

    public void deleteAllRasci(long id) {
        Task task = getTaskById(id);
        for(Rasci r : task.getRasciList()){
            rasciRepository.delete(r);
        }
    }

    public Task createNewConfiguration(long userId, long taskId, long projectId) {
        Task task = getTaskById(taskId);
        if(task == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        if(projectId == -1){
            return configurationTaskService.createNewConfiguration(task, new ConfigurationHelper(), null, user, null);
        }
        Project project = projectService.getProjectById(projectId);
        if(project != null && ItemUsersUtil.getAllUsersCanEdit(project).contains(user)) {
            return configurationTaskService.createNewConfiguration(task, new ConfigurationHelper(), null, user, project);
        }
        return null;
    }
}
