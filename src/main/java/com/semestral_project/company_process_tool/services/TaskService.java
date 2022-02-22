package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.Process;
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
    @Autowired
    BPMNparser bpmNparser;

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
            bpmNparser.updateTaskInAllWorkflows(task_, true,false, task_.getTaskType(),null);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public boolean removeTaskById(long id){
        try {
            Task task = taskRepository.findById(id).get();
            if(bpmNparser.removeTaskFromAllWorkflows(task)){
                var list = task.getMandatoryInputs();
                for(WorkItem w : list){
                    var list2 = w.getAsMandatoryInput();
                    list2.remove(task);
                    w.setAsMandatoryInput(list2);
                    workItemRepository.save(w);
                }
                list = task.getOptionalInputs();
                for(WorkItem w : list){
                    var list2 = w.getAsOptionalInput();
                    list2.remove(task);
                    w.setAsOptionalInput(list2);
                    workItemRepository.save(w);
                }
                list = task.getOutputs();
                for(WorkItem w : list){
                    var list2 = w.getAsOutput();
                    list2.remove(task);
                    w.setAsOutput(list2);
                    workItemRepository.save(w);
                }
                list = task.getGuidanceWorkItems();
                for(WorkItem w : list){
                    var list2 = w.getAsGuidanceWorkItem();
                    list2.remove(task);
                    w.setAsGuidanceWorkItem(list2);
                    workItemRepository.save(w);
                }
                taskRepository.deleteById(id);
                return true;
            }
            return false;
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
                guidanceList.remove(item_);

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
                bpmNparser.removeInputConnectionFromAllWorkflows(task_,item_);
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
                bpmNparser.removeOutputConnectionFromAllWorkflows(task_,item_);
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

    public List<Task> getAllTemplates(){
        List<Task> allTemplates = taskRepository.findAllTasksTemplates();
        return allTemplates;
    }


}
