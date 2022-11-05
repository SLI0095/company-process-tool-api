package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.*;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    UserRepository userRepository;
    @Autowired
    SnapshotTaskService snapshotTaskService;

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
        if(taskData.isPresent()) {
            return taskData.get();
        }
        else return null;
    }

    public long addTask(Task task, long userId) {
        try {
            if (userRepository.existsById(userId)) {
                User user = userRepository.findById(userId).get();
                task.setOwner(user);
                var list = task.getCanEdit();
                list.add(user);
                task = taskRepository.save(task);
                return task.getId();
            } else return -1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public int addAccess(long taskId, long whoEdits, User getAccess){
        Optional<Task> taskData = taskRepository.findById(taskId);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(getAccess.getId()).get();
                if(task_.getHasAccess().contains(getAccess_)) {
                    return 3; //already has access
                }
                if(task_.getCanEdit().contains(getAccess_)){
                    var list = task_.getCanEdit();
                    if(list.size() == 1){
                        return 6;
                    }
                    list.remove(getAccess_);
                    task_.setCanEdit(list);
                }
                    var list = task_.getHasAccess();
                    list.add(getAccess_);
                    task_.setHasAccess(list);
                    taskRepository.save(task_);
                    return 1; //OK
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }


    public int addAccessAutomatic(long taskId, UserType getAccess){
        if(!(getAccess instanceof User)){
            return 1;
        }
        Optional<Task> taskData = taskRepository.findById(taskId);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();

            User getAccess_ = userRepository.findById(getAccess.getId()).get();
            if (task_.getHasAccess().contains(getAccess_)) {
                return 3; //already has access
            }
            if (task_.getCanEdit().contains(getAccess_)) {
                var list = task_.getCanEdit();
                list.remove(getAccess_);
                task_.setCanEdit(list);
            }
            var list = task_.getHasAccess();
            list.add(getAccess_);
            task_.setHasAccess(list);
            taskRepository.save(task_);
            return 1; //OK
        }
        else
        {
            return 2; //role not found
        }
    }

    public int removeAccess(long taskId, long whoEdits, User removeAccess){
        Optional<Task> taskData = taskRepository.findById(taskId);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(removeAccess.getId()).get();
                if(task_.getHasAccess().contains(getAccess_)) {
                    var list = task_.getHasAccess();
                    list.remove(getAccess_);
                    task_.setHasAccess(list);
                    taskRepository.save(task_);
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

    public int removeEdit(long taskId, long whoEdits, User removeEdit){
        Optional<Task> taskData = taskRepository.findById(taskId);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)){
                User removeEdit_ = userRepository.findById(removeEdit.getId()).get();
                if(task_.getCanEdit().contains(removeEdit_)) {
                    var list = task_.getCanEdit();
                    if(list.size() == 1){
                        return 6;
                    }
                    list.remove(removeEdit_);
                    task_.setCanEdit(list);
                    taskRepository.save(task_);
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

    public int addEdit(long taskId, long whoEdits, User getEdit){
        Optional<Task> taskData = taskRepository.findById(taskId);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)){
                User getEdit_ = userRepository.findById(getEdit.getId()).get();
                if(task_.getCanEdit().contains(getEdit_)){
                    return 4; //already can edit
                } else if(task_.getHasAccess().contains(getEdit_)) {
                    var list = task_.getHasAccess();
                    list.remove(getEdit_);
                    task_.setHasAccess(list);
                    list = task_.getCanEdit();
                    list.add(getEdit_);
                    task_.setCanEdit(list);
                    taskRepository.save(task_);
                    return 1; //OK
                } else{
                    var list = task_.getCanEdit();
                    list.add(getEdit_);
                    task_.setCanEdit(list);
                    taskRepository.save(task_);
                    return 1; //OK
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int addEditAutomatic(long taskId, UserType getEdit){
        if(!(getEdit instanceof User)){
            return 1;
        }
        Optional<Task> taskData = taskRepository.findById(taskId);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User getEdit_ = userRepository.findById(getEdit.getId()).get();
            if (task_.getCanEdit().contains(getEdit_)) {
                return 4; //already can edit
            } else if (task_.getHasAccess().contains(getEdit_)) {
                var list = task_.getHasAccess();
                list.remove(getEdit_);
                task_.setHasAccess(list);
                list = task_.getCanEdit();
                list.add(getEdit_);
                task_.setCanEdit(list);
                taskRepository.save(task_);
                return 1; //OK
            } else {
                var list = task_.getCanEdit();
                list.add(getEdit_);
                task_.setCanEdit(list);
                taskRepository.save(task_);
                return 1; //OK
            }
        }
        else
        {
            return 2; //role not found
        }
    }


    public int updateTask(long id, Task task, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                task_ = fillTask(task_, task);
                taskRepository.save(task_);
                bpmNparser.updateTaskInAllWorkflows(task_, true, false, task_.getTaskType(), null);
                return 1;
            }
            return 3;
        }
        else
        {
            return 2;
        }
    }

    public int removeTaskById(long id, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task.getCanEdit().contains(whoEdits_)) {
                if (bpmNparser.removeTaskFromAllWorkflows(task)) {
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
                    taskRepository.deleteById(id);
                    return 1;
                }
            } else return 3; //cannot edit
        }
        return 2;
    }

    public int addTaskStep(long id, TaskStep taskStep, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                taskStep.setTask(task_);
                taskStepRepository.save(taskStep);

                return 1;
            }
            return 3; //cannot edit
        }
        else
        {
            return 2;
        }
    }

    public int removeTaskStep(long id, TaskStep taskStep, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                TaskStep step_ = taskStepRepository.findById(taskStep.getId()).get();
                var stepsList = task_.getSteps();
                if(step_.getTask().getId() == task_.getId())
                {
                    taskStepRepository.delete(step_);
                    return 1;
                }
                else {
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


    public int addRasci(long id, Rasci rasci, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                List<Rasci> rasciList = task_.getRasciList();
                for (Rasci r : rasciList) {
                    if (r.getRole().getId() == rasci.getRole().getId())
                        return 4;
                }
                rasci.setTask(task_);
                rasciRepository.save(rasci);

                return 1;
            }
            return 3;
        }
        else
        {
            return 2;
        }
    }

    public int removeRasci(long id, Rasci rasci, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                rasciRepository.delete(rasci);
                return 1;
            }
            return 3;
        }
        else
        {
            return 2;
        }
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
        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                List<WorkItem> inputList = task_.getMandatoryInputs();
                if (inputList.contains(item_)) {
                    return 4;
                }
                List<Task> tasksList = item_.getAsMandatoryInput();
                tasksList.add(task_);
                item_.setAsMandatoryInput(tasksList);
                workItemRepository.save(item_);
                return 1;
            }
            return 3;
        } else {
            return 2;
        }
    }

    public int addMandatoryInputWithoutUser(long id, WorkItem workItem) {
        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();

            List<WorkItem> inputList = task_.getMandatoryInputs();
            if (inputList.contains(item_)) {
                return 4;
            }
            List<Task> tasksList = item_.getAsMandatoryInput();
            tasksList.add(task_);
            item_.setAsMandatoryInput(tasksList);
            workItemRepository.save(item_);
            return 1;

        } else {
            return 2;
        }
    }

    public int removeMandatoryInput(long id, WorkItem workItem, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                List<WorkItem> inputList = task_.getMandatoryInputs();
                if (inputList.contains(item_)) {
                    bpmNparser.removeInputConnectionFromAllWorkflows(task_, item_);
                    List<Task> tasksList = item_.getAsMandatoryInput();
                    tasksList.remove(task_);
                    item_.setAsMandatoryInput(tasksList);
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
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                List<WorkItem> outputList = task_.getOutputs();
                if (outputList.contains(item_)) {
                    return 4;
                }
                List<Task> tasksList = item_.getAsOutput();
                tasksList.add(task_);
                item_.setAsOutput(tasksList);
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

    public int addOutputWithoutUser(long id, WorkItem workItem){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();

            List<WorkItem> outputList = task_.getOutputs();
            if (outputList.contains(item_)) {
                return 4;
            }
            List<Task> tasksList = item_.getAsOutput();
            tasksList.add(task_);
            item_.setAsOutput(tasksList);
            workItemRepository.save(item_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int removeOutput(long id, WorkItem workItem, long whoEdits){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(task_.getCanEdit().contains(whoEdits_)) {
                List<WorkItem> outputList = task_.getOutputs();
                if (outputList.contains(item_)) {
                    bpmNparser.removeOutputConnectionFromAllWorkflows(task_, item_);
                    List<Task> tasksList = item_.getAsOutput();
                    tasksList.remove(task_);
                    item_.setAsOutput(tasksList);
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
    }

    public List<Task> getAllTemplates(long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            List<Task> allTemplates = taskRepository.findAllTasksTemplatesForUser(user);
            return allTemplates;
        }else return null;
    }

    public List<Task> getAllTemplatesCanEdit(long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            List<Task> allTemplates = taskRepository.findAllTasksTemplatesForUserCanEdit(user);
            return allTemplates;
        }else return null;
    }


    public int createSnapshot(Long id, long userId, String description) {
        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isPresent()) {
            Task task_ = taskData.get();
            User whoEdits_ = userRepository.findById(userId).get();
            if (task_.getCanEdit().contains(whoEdits_)) {
                snapshotTaskService.createSnapshot(task_, description);
                return 1;
            }
            return 3;
        } else {
            return 2;
        }
    }
}
