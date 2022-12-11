package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotElement;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotTask;
import com.semestral_project.company_process_tool.repositories.*;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotTaskService;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotsHelper;
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

//        try {
//            if (userRepository.existsById(userId)) {
//                User user = userRepository.findById(userId).get();
//                task.setOwner(user);
//                var list = task.getCanEdit();
//                list.add(user);
//                task = taskRepository.save(task);
//                return task.getId();
//            } else return -1;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return -1;
//        }
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



//        Optional<Task> taskData = taskRepository.findById(taskId);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)){
//                User getAccess_ = userRepository.findById(getAccess.getId()).get();
//                if(task_.getHasAccess().contains(getAccess_)) {
//                    return 3; //already has access
//                }
//                if(task_.getCanEdit().contains(getAccess_)){
//                    var list = task_.getCanEdit();
//                    if(list.size() == 1){
//                        return 6;
//                    }
//                    list.remove(getAccess_);
//                    task_.setCanEdit(list);
//                }
//                    var list = task_.getHasAccess();
//                    list.add(getAccess_);
//                    task_.setHasAccess(list);
//                    taskRepository.save(task_);
//                    return 1; //OK
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
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


//        if(!(getAccess instanceof User)){
//            return 1;
//        }
//        Optional<Task> taskData = taskRepository.findById(taskId);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//
//            User getAccess_ = userRepository.findById(getAccess.getId()).get();
//            if (task_.getHasAccess().contains(getAccess_)) {
//                return 3; //already has access
//            }
//            if (task_.getCanEdit().contains(getAccess_)) {
//                var list = task_.getCanEdit();
//                list.remove(getAccess_);
//                task_.setCanEdit(list);
//            }
//            var list = task_.getHasAccess();
//            list.add(getAccess_);
//            task_.setHasAccess(list);
//            taskRepository.save(task_);
//            return 1; //OK
//        }
//        else
//        {
//            return 2; //role not found
//        }
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

//        Optional<Task> taskData = taskRepository.findById(taskId);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)){
//                User getAccess_ = userRepository.findById(removeAccess.getId()).get();
//                if(task_.getHasAccess().contains(getAccess_)) {
//                    var list = task_.getHasAccess();
//                    list.remove(getAccess_);
//                    task_.setHasAccess(list);
//                    taskRepository.save(task_);
//                    return 1; //access removed
//                } else{
//                    return 3; //nothing to remove
//                }
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
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


//        Optional<Task> taskData = taskRepository.findById(taskId);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)){
//                User removeEdit_ = userRepository.findById(removeEdit.getId()).get();
//                if(task_.getCanEdit().contains(removeEdit_)) {
//                    var list = task_.getCanEdit();
//                    if(list.size() == 1){
//                        return 6;
//                    }
//                    list.remove(removeEdit_);
//                    task_.setCanEdit(list);
//                    taskRepository.save(task_);
//                    return 1; //edit removed
//                } else{
//                    return 3; //nothing to remove
//                }
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
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

//        Optional<Task> taskData = taskRepository.findById(taskId);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)){
//                User getEdit_ = userRepository.findById(getEdit.getId()).get();
//                if(task_.getCanEdit().contains(getEdit_)){
//                    return 4; //already can edit
//                } else if(task_.getHasAccess().contains(getEdit_)) {
//                    var list = task_.getHasAccess();
//                    list.remove(getEdit_);
//                    task_.setHasAccess(list);
//                    list = task_.getCanEdit();
//                    list.add(getEdit_);
//                    task_.setCanEdit(list);
//                    taskRepository.save(task_);
//                    return 1; //OK
//                } else{
//                    var list = task_.getCanEdit();
//                    list.add(getEdit_);
//                    task_.setCanEdit(list);
//                    taskRepository.save(task_);
//                    return 1; //OK
//                }
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
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

//        if(!(getEdit instanceof User)){
//            return 1;
//        }
//        Optional<Task> taskData = taskRepository.findById(taskId);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User getEdit_ = userRepository.findById(getEdit.getId()).get();
//            if (task_.getCanEdit().contains(getEdit_)) {
//                return 4; //already can edit
//            } else if (task_.getHasAccess().contains(getEdit_)) {
//                var list = task_.getHasAccess();
//                list.remove(getEdit_);
//                task_.setHasAccess(list);
//                list = task_.getCanEdit();
//                list.add(getEdit_);
//                task_.setCanEdit(list);
//                taskRepository.save(task_);
//                return 1; //OK
//            } else {
//                var list = task_.getCanEdit();
//                list.add(getEdit_);
//                task_.setCanEdit(list);
//                taskRepository.save(task_);
//                return 1; //OK
//            }
//        }
//        else
//        {
//            return 2; //role not found
//        }
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

//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                task_ = fillTask(task_, task);
//                taskRepository.save(task_);
//                bpmNparser.updateTaskInAllWorkflows(task_, true, false, task_.getTaskType(), null);
//                return 1;
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
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


//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task.getCanEdit().contains(whoEdits_)) {
//                if (bpmNparser.removeTaskFromAllWorkflows(task)) {
//                    var list = task.getMandatoryInputs();
//                    for (WorkItem w : list) {
//                        var list2 = w.getAsMandatoryInput();
//                        list2.remove(task);
//                        w.setAsMandatoryInput(list2);
//                        workItemRepository.save(w);
//                    }
//                    list = task.getOutputs();
//                    for (WorkItem w : list) {
//                        var list2 = w.getAsOutput();
//                        list2.remove(task);
//                        w.setAsOutput(list2);
//                        workItemRepository.save(w);
//                    }
//                    for(SnapshotElement snapshot : task.getSnapshots()){
//                        snapshot.setOriginalElement(null);
//                    }
//                    taskRepository.deleteById(id);
//                    return 1;
//                }
//            } else return 3; //cannot edit
//        }
//        return 2;
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

//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                taskStep.setTask(task_);
//                taskStepRepository.save(taskStep);
//
//                return 1;
//            }
//            return 3; //cannot edit
//        }
//        else
//        {
//            return 2;
//        }
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

//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                TaskStep step_ = taskStepRepository.findById(taskStep.getId()).get();
//                var stepsList = task_.getSteps();
//                if(step_.getTask().getId() == task_.getId())
//                {
//                    taskStepRepository.delete(step_);
//                    return 1;
//                }
//                else {
//                    return 4;
//                }
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
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

//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                List<Rasci> rasciList = task_.getRasciList();
//                for (Rasci r : rasciList) {
//                    if (r.getRole().getId() == rasci.getRole().getId())
//                        return 4; //role already in RASCI
//                }
//                rasci.setTask(task_);
//                rasciRepository.save(rasci);
//
//                return 1;
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
    }

    public int removeRasci(long id, Rasci rasci, long whoEdits){
        Task task = getTaskById(id);
        if (task == null){
            return  2; //task not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 3; //cannot edit
        }
        rasciRepository.delete(rasci);
        return 1;

//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                rasciRepository.delete(rasci);
//                return 1;
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
    }

//    public int addGuidanceWorkItem(long id, WorkItem workItem, long whoEdits){
//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                List<WorkItem> guidanceList = task_.getGuidanceWorkItems();
//                if (guidanceList.contains(item_)) {
//                    return 4;
//                }
//                List<Task> tasksList = item_.getAsGuidanceWorkItem();
//                tasksList.add(task_);
//                item_.setAsGuidanceWorkItem(tasksList);
//                workItemRepository.save(item_);
//                return 1;
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
//    }
//
//    public int removeGuidanceWorkItem(long id, WorkItem workItem, long whoEdits){
//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                List<WorkItem> guidanceList = task_.getGuidanceWorkItems();
//                if (guidanceList.contains(item_)) {
//                    guidanceList.remove(item_);
//
//                    List<Task> tasksList = item_.getAsGuidanceWorkItem();
//                    tasksList.remove(task_);
//                    item_.setAsGuidanceWorkItem(tasksList);
//                    workItemRepository.save(item_);
//                    return 1;
//
//                } else {
//                    return 4;
//                }
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
//    }

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


//        Optional<Task> taskData = taskRepository.findById(id);
//        if (taskData.isPresent()) {
//            Task task_ = taskData.get();
//            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                List<WorkItem> inputList = task_.getMandatoryInputs();
//                if (inputList.contains(item_)) {
//                    return 4;
//                }
//                List<Task> tasksList = item_.getAsMandatoryInput();
//                tasksList.add(task_);
//                item_.setAsMandatoryInput(tasksList);
//                workItemRepository.save(item_);
//                return 1;
//            }
//            return 3;
//        } else {
//            return 2;
//        }
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
        workItemRepository.save(item);


//        Optional<Task> taskData = taskRepository.findById(id);
//        if (taskData.isPresent()) {
//            Task task_ = taskData.get();
//            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
//
//            List<WorkItem> inputList = task_.getMandatoryInputs();
//            if (inputList.contains(item_)) {
//                return 4;
//            }
//            List<Task> tasksList = item_.getAsMandatoryInput();
//            tasksList.add(task_);
//            item_.setAsMandatoryInput(tasksList);
//            workItemRepository.save(item_);
//            return 1;
//
//        } else {
//            return 2;
//        }
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


//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                List<WorkItem> inputList = task_.getMandatoryInputs();
//                if (inputList.contains(item_)) {
//                    bpmNparser.removeInputConnectionFromAllWorkflows(task_, item_);
//                    List<Task> tasksList = item_.getAsMandatoryInput();
//                    tasksList.remove(task_);
//                    item_.setAsMandatoryInput(tasksList);
//                    workItemRepository.save(item_);
//
//                    return 1;
//
//                } else {
//                    return 4;
//                }
//            }
//            return 3;
//
//        }
//        else
//        {
//            return 2;
//        }
    }

  /*  public int addOptionalInput(long id, WorkItem workItem, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                List<WorkItem> inputList = task_.getOptionalInputs();
                if (inputList.contains(item_)) {
                    return 4;
                }
                List<Task> tasksList = item_.getAsOptionalInput();
                tasksList.add(task_);
                item_.setAsOptionalInput(tasksList);
                workItemRepository.save(item_);
                return 1;
            }
            return 3;
        }
        else
        {
            return 2;
        }
    }

    public int removeOptionalInput(long id, WorkItem workItem, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                List<WorkItem> inputList = task_.getOptionalInputs();
                if (inputList.contains(item_)) {
                    List<Task> tasksList = item_.getAsOptionalInput();
                    tasksList.remove(task_);
                    item_.setAsOptionalInput(tasksList);
                    workItemRepository.save(item_);
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
    }*/

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

//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                List<WorkItem> outputList = task_.getOutputs();
//                if (outputList.contains(item_)) {
//                    return 4;
//                }
//                List<Task> tasksList = item_.getAsOutput();
//                tasksList.add(task_);
//                item_.setAsOutput(tasksList);
//                workItemRepository.save(item_);
//                return 1;
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
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
        workItemRepository.save(item);

//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
//
//            List<WorkItem> outputList = task_.getOutputs();
//            if (outputList.contains(item_)) {
//                return;
//            }
//            List<Task> tasksList = item_.getAsOutput();
//            tasksList.add(task_);
//            item_.setAsOutput(tasksList);
//            workItemRepository.save(item_);
//        }
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

//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(task_.getCanEdit().contains(whoEdits_)) {
//                List<WorkItem> outputList = task_.getOutputs();
//                if (outputList.contains(item_)) {
//                    bpmNparser.removeOutputConnectionFromAllWorkflows(task_, item_);
//                    List<Task> tasksList = item_.getAsOutput();
//                    tasksList.remove(task_);
//                    item_.setAsOutput(tasksList);
//                    workItemRepository.save(item_);
//                    return 1;
//
//                } else {
//                    return 4;
//                }
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
    }

    public List<Task> getAllUserCanView(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Task> ret = new HashSet<>();
        List<Task> roles = (List<Task>) taskRepository.findAll();
        for(Task t : roles){
            if(ItemUsersUtil.getAllUsersCanView(t).contains(user)){
                ret.add(t);
            }
        }
        return new ArrayList<>(ret);


//        if(userRepository.existsById(userId)) {
//            User user = userRepository.findById(userId).get();
//            return taskRepository.findAllTasksTemplatesForUser(user);
//        }else return null;
    }

    public List<Task> getAllUserCanEdit(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Task> ret = new HashSet<>();
        List<Task> roles = (List<Task>) taskRepository.findAll();
        for(Task t : roles){
            if(ItemUsersUtil.getAllUsersCanEdit(t).contains(user)){
                ret.add(t);
            }
        }
        return new ArrayList<>(ret);

//        if(userRepository.existsById(userId)) {
//            User user = userRepository.findById(userId).get();
//            return taskRepository.findAllTasksTemplatesForUserCanEdit(user);
//        }else return null;
    }

    public List<Task> getAllUserCanViewFiltered(long userId, boolean isTemplate) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        HashSet<Task> ret = new HashSet<>();
        List<Task> roles = taskRepository.findByIsTemplate(isTemplate);
        for (Task t : roles) {
            if (ItemUsersUtil.getAllUsersCanView(t).contains(user)) {
                ret.add(t);
            }
        }
        return new ArrayList<>(ret);
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
}
