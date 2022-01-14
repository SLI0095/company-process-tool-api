package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.entities.Task;
import com.semestral_project.company_process_tool.entities.TaskStep;
import com.semestral_project.company_process_tool.entities.WorkItem;
import com.semestral_project.company_process_tool.repositories.RoleRepository;
import com.semestral_project.company_process_tool.repositories.TaskRepository;
import com.semestral_project.company_process_tool.repositories.TaskStepRepository;
import com.semestral_project.company_process_tool.repositories.WorkItemRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskStepRepository taskStepRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    WorkItemRepository workItemRepository;

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getTasks() {
        try {
            return org.springframework.http.ResponseEntity.ok((List<Task>) taskRepository.findAll());
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> taskById(@PathVariable Long id){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            return ResponseEntity.ok(taskData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/tasks")
    public ResponseEntity<ResponseMessage> addTask(@RequestBody Task task) {
        try {
            taskRepository.save(task);
            return ResponseEntity.ok(new ResponseMessage("Task added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task could not be added"));
        }
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<ResponseMessage> updateTask(@PathVariable Long id, @RequestBody Task task){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            task_ = fillTask(task_, task);

            taskRepository.save(task_);
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/addStep")
    public ResponseEntity<ResponseMessage> addTaskStep(@PathVariable Long id, @RequestBody TaskStep taskStep){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            TaskStep step_ = taskStepRepository.findById(taskStep.getId()).get();
            var stepsList = task_.getSteps();
            if(stepsList.contains(step_))
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Step already added"));
            }
            stepsList.add(step_);
            task_.setSteps(stepsList);

            taskRepository.save(task_);
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/removeStep")
    public ResponseEntity<ResponseMessage> removeTaskStep(@PathVariable Long id, @RequestBody TaskStep taskStep){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            TaskStep step_ = taskStepRepository.findById(taskStep.getId()).get();
            var stepsList = task_.getSteps();
            if(stepsList.contains(step_))
            {
                stepsList.remove(step_);
                task_.setSteps(stepsList);

                taskRepository.save(task_);
                return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Step removed."));
            }
            else {
                return ResponseEntity.badRequest().body(new ResponseMessage("Step not in task id: " + id));
            }

        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/addPrimaryPerformer")
    public ResponseEntity<ResponseMessage> addPrimaryPerformer(@PathVariable Long id, @RequestBody Role role){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            Role role_ = roleRepository.findById(role.getId()).get();
            var roleList = task_.getPrimaryPerformers();
            if(roleList.contains(role_))
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Performer already added"));
            }
            roleList.add(role_);
            task_.setPrimaryPerformers(roleList);

            taskRepository.save(task_);
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Performer added."));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/removePrimaryPerformer")
    public ResponseEntity<ResponseMessage> removePrimaryPerformer(@PathVariable Long id, @RequestBody Role role){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            Role role_ = roleRepository.findById(role.getId()).get();
            var roleList = task_.getPrimaryPerformers();
            if(roleList.contains(role_)) {
                roleList.remove(role_);
                task_.setPrimaryPerformers(roleList);
                taskRepository.save(task_);
                return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Performer removed."));

            }
            else {
                return ResponseEntity.badRequest().body(new ResponseMessage("Performer not in task id: " + id));
            }

        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/addAdditionalPerformer")
    public ResponseEntity<ResponseMessage> addAdditionalPerformer(@PathVariable Long id, @RequestBody Role role){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            Role role_ = roleRepository.findById(role.getId()).get();
            var roleList = task_.getAdditionalPerformers();
            if(roleList.contains(role_))
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Performer already added"));
            }
            roleList.add(role_);
            task_.setAdditionalPerformers(roleList);

            taskRepository.save(task_);
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Performer added."));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/removeAdditionalPerformer")
    public ResponseEntity<ResponseMessage> removeAdditionalPerformer(@PathVariable Long id, @RequestBody Role role){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            Role role_ = roleRepository.findById(role.getId()).get();
            var roleList = task_.getAdditionalPerformers();
            if(roleList.contains(role_)) {
                roleList.remove(role_);
                task_.setAdditionalPerformers(roleList);
                taskRepository.save(task_);
                return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Performer removed."));

            }
            else {
                return ResponseEntity.badRequest().body(new ResponseMessage("Performer not in task id: " + id));
            }

        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/addMandatoryInput")
    public ResponseEntity<ResponseMessage> addMandatoryInput(@PathVariable Long id, @RequestBody WorkItem item){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(item.getId()).get();
            var inputList = task_.getMandatoryInputs();
            if(inputList.contains(item_))
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Mandatory input already added"));
            }
            inputList.add(item_);
            task_.setMandatoryInputs(inputList);

            taskRepository.save(task_);
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Mandatory input added."));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }


    @PutMapping("/tasks/{id}/removeMandatoryInput")
    public ResponseEntity<ResponseMessage> removeMandatoryInput(@PathVariable Long id, @RequestBody WorkItem item){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(item.getId()).get();
            var inputList = task_.getMandatoryInputs();
            if(inputList.contains(item_)) {
                inputList.remove(item_);
                task_.setMandatoryInputs(inputList);
                taskRepository.save(task_);
                return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Mandatory input removed."));

            }
            else {
                return ResponseEntity.badRequest().body(new ResponseMessage("Mandatory input not in task id: " + id));
            }

        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/addOptionalInput")
    public ResponseEntity<ResponseMessage> addOptionalInput(@PathVariable Long id, @RequestBody WorkItem item){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(item.getId()).get();
            var inputList = task_.getOptionalInputs();
            if(inputList.contains(item_))
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Optional input already added"));
            }
            inputList.add(item_);
            task_.setOptionalInputs(inputList);

            taskRepository.save(task_);
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Optional input added."));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }


    @PutMapping("/tasks/{id}/removeOptionalInput")
    public ResponseEntity<ResponseMessage> removeOptionalInput(@PathVariable Long id, @RequestBody WorkItem item){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(item.getId()).get();
            var inputList = task_.getOptionalInputs();
            if(inputList.contains(item_)) {
                inputList.remove(item_);
                task_.setOptionalInputs(inputList);
                taskRepository.save(task_);
                return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Optional input removed."));

            }
            else {
                return ResponseEntity.badRequest().body(new ResponseMessage("Optional input not in task id: " + id));
            }

        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/addOutput")
    public ResponseEntity<ResponseMessage> addOutput(@PathVariable Long id, @RequestBody WorkItem item){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(item.getId()).get();
            var outputList = task_.getOutputs();
            if(outputList.contains(item_))
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Output already added"));
            }
            outputList.add(item_);
            task_.setOutputs(outputList);

            taskRepository.save(task_);
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Output added."));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }


    @PutMapping("/tasks/{id}/removeOutput")
    public ResponseEntity<ResponseMessage> removeOutput(@PathVariable Long id, @RequestBody WorkItem item){
        Optional<Task> taskData = taskRepository.findById(id);
        if(taskData.isPresent()) {
            Task task_ = taskData.get();
            WorkItem item_ = workItemRepository.findById(item.getId()).get();
            var outputList = task_.getOutputs();
            if(outputList.contains(item_)) {
                outputList.remove(item_);
                task_.setOptionalInputs(outputList);
                taskRepository.save(task_);
                return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Output removed."));

            }
            else {
                return ResponseEntity.badRequest().body(new ResponseMessage("Output not in task id: " + id));
            }

        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }



    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ResponseMessage> removeTask(@PathVariable Long id) {
        try {
            taskRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

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
}
