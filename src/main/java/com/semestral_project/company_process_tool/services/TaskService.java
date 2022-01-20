package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.Rasci;
import com.semestral_project.company_process_tool.entities.Task;
import com.semestral_project.company_process_tool.entities.TaskStep;
import com.semestral_project.company_process_tool.entities.WorkItem;
import com.semestral_project.company_process_tool.repositories.*;
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

    private Task fillTask(Task oldTask, Task updatedTask){
        oldTask.setName(updatedTask.getName());
        oldTask.setBriefDescription(updatedTask.getBriefDescription());
        oldTask.setMainDescription(updatedTask.getMainDescription());
        oldTask.setVersion(updatedTask.getVersion());
        oldTask.setChangeDate(updatedTask.getChangeDate());
        oldTask.setChangeDescription(updatedTask.getChangeDescription());
        oldTask.setPurpose(updatedTask.getPurpose());
        oldTask.setKeyConsiderations(updatedTask.getKeyConsiderations());
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

    public boolean addTask(Task task){
        try {
            taskRepository.save(task);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int updateTask(long id, Task task){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            task_ = fillTask(task_, task);

            taskRepository.save(task_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public boolean removeTaskById(long id){
        try {
            taskRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int addTaskStep(long id, TaskStep taskStep){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();

            taskStep.setTask(task_);
            taskStepRepository.save(taskStep);

            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int removeTaskStep(long id, TaskStep taskStep){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            TaskStep step_ = taskStepRepository.findById(taskStep.getId()).get();
            var stepsList = task_.getSteps();
            if(step_.getTask().getId() == task_.getId())
            {
                taskStepRepository.delete(step_);
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

    public int addRasci(long id, Rasci rasci){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            List<Rasci> rasciList = task_.getRasciList();
            for(Rasci r : rasciList){
                if(r.getRole().getId() == rasci.getRole().getId())
                    return 3;
            }
            rasci.setElement(task_);
            rasciRepository.save(rasci);

            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int removeRasci(long id, Rasci rasci){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            rasciRepository.delete(rasci);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int addGuidanceWorkItem(long id, WorkItem workItem){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            List<WorkItem> guidanceList = task_.getGuidanceWorkItems();
            if(guidanceList.contains(item_))
            {
                return 3;
            }
            List<Task> tasksList = item_.getAsGuidanceWorkItem();
            tasksList.add(task_);
            item_.setAsGuidanceWorkItem(tasksList);
            workItemRepository.save(item_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int removeGuidanceWorkItem(long id, WorkItem workItem){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            List<WorkItem> guidanceList = task_.getGuidanceWorkItems();
            if(guidanceList.contains(item_)) {
                List<Task> tasksList = item_.getAsGuidanceWorkItem();
                tasksList.remove(task_);
                item_.setAsGuidanceWorkItem(tasksList);
                workItemRepository.save(item_);
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

    public int addMandatoryInput(long id, WorkItem workItem) {
        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            List<WorkItem> inputList = task_.getMandatoryInputs();
            if (inputList.contains(item_)) {
                return 3;
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

    public int removeMandatoryInput(long id, WorkItem workItem){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            List<WorkItem> inputList = task_.getMandatoryInputs();
            if(inputList.contains(item_)) {
                List<Task> tasksList = item_.getAsMandatoryInput();
                tasksList.remove(task_);
                item_.setAsMandatoryInput(tasksList);
                workItemRepository.save(item_);

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

    public int addOptionalInput(long id, WorkItem workItem){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            List<WorkItem> inputList = task_.getOptionalInputs();
            if(inputList.contains(item_))
            {
                return 3;
            }
            List<Task> tasksList = item_.getAsOptionalInput();
            tasksList.add(task_);
            item_.setAsOptionalInput(tasksList);
            workItemRepository.save(item_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int removeOptionalInput(long id, WorkItem workItem){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            List<WorkItem> inputList = task_.getOptionalInputs();
            if(inputList.contains(item_)) {
                List<Task> tasksList = item_.getAsOptionalInput();
                tasksList.remove(task_);
                item_.setAsOptionalInput(tasksList);
                workItemRepository.save(item_);
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

    public int addOutput(long id, WorkItem workItem){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            List<WorkItem> outputList = task_.getOutputs();
            if(outputList.contains(item_))
            {
                return 3;
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

    public int removeOutput(long id, WorkItem workItem){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            List<WorkItem> outputList = task_.getOutputs();
            if(outputList.contains(item_)) {
                List<Task> tasksList = item_.getAsOutput();
                tasksList.remove(task_);
                item_.setAsOutput(tasksList);
                workItemRepository.save(item_);
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

    //    @PutMapping("/tasks/{id}/addPrimaryPerformer")
//    public ResponseEntity<ResponseMessage> addPrimaryPerformer(@PathVariable Long id, @RequestBody Role role){
//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            Role role_ = roleRepository.findById(role.getId()).get();
//            var roleList = task_.getPrimaryPerformers();
//            if(roleList.contains(role_))
//            {
//                return ResponseEntity.badRequest().body(new ResponseMessage("Performer already added"));
//            }
//            roleList.add(role_);
//            task_.setPrimaryPerformers(roleList);
//
//            taskRepository.save(task_);
//            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Performer added."));
//        }
//        else
//        {
//            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
//        }
//    }
//
//    @PutMapping("/tasks/{id}/removePrimaryPerformer")
//    public ResponseEntity<ResponseMessage> removePrimaryPerformer(@PathVariable Long id, @RequestBody Role role){
//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            Role role_ = roleRepository.findById(role.getId()).get();
//            var roleList = task_.getPrimaryPerformers();
//            if(roleList.contains(role_)) {
//                roleList.remove(role_);
//                task_.setPrimaryPerformers(roleList);
//                taskRepository.save(task_);
//                return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Performer removed."));
//
//            }
//            else {
//                return ResponseEntity.badRequest().body(new ResponseMessage("Performer not in task id: " + id));
//            }
//
//        }
//        else
//        {
//            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
//        }
//    }
//
//    @PutMapping("/tasks/{id}/addAdditionalPerformer")
//    public ResponseEntity<ResponseMessage> addAdditionalPerformer(@PathVariable Long id, @RequestBody Role role){
//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            Role role_ = roleRepository.findById(role.getId()).get();
//            var roleList = task_.getAdditionalPerformers();
//            if(roleList.contains(role_))
//            {
//                return ResponseEntity.badRequest().body(new ResponseMessage("Performer already added"));
//            }
//            roleList.add(role_);
//            task_.setAdditionalPerformers(roleList);
//
//            taskRepository.save(task_);
//            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Performer added."));
//        }
//        else
//        {
//            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
//        }
//    }
//
//    @PutMapping("/tasks/{id}/removeAdditionalPerformer")
//    public ResponseEntity<ResponseMessage> removeAdditionalPerformer(@PathVariable Long id, @RequestBody Role role){
//        Optional<Task> taskData = taskRepository.findById(id);
//        if(taskData.isPresent()) {
//            Task task_ = taskData.get();
//            Role role_ = roleRepository.findById(role.getId()).get();
//            var roleList = task_.getAdditionalPerformers();
//            if(roleList.contains(role_)) {
//                roleList.remove(role_);
//                task_.setAdditionalPerformers(roleList);
//                taskRepository.save(task_);
//                return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Performer removed."));
//
//            }
//            else {
//                return ResponseEntity.badRequest().body(new ResponseMessage("Performer not in task id: " + id));
//            }
//
//        }
//        else
//        {
//            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
//        }
//    }

}
