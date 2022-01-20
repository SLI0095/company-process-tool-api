package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.services.TaskService;
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
    TaskService taskService;

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getTasks() {
        List<Task> tasks = taskService.getAllTasks();
        if(tasks != null){
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> taskById(@PathVariable Long id){

        Task task = taskService.getTaskById(id);
        if(task != null){
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<ResponseMessage> addTask(@RequestBody Task task) {
        boolean ret = taskService.addTask(task);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Task added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task could not be added"));
        }
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<ResponseMessage> updateTask(@PathVariable Long id, @RequestBody Task task){
        int ret = taskService.updateTask(id, task);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ResponseMessage> removeTask(@PathVariable Long id) {
        boolean ret = taskService.removeTaskById(id);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is deleted"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " could not be deleted"));
        }
    }

    @PutMapping("/tasks/{id}/addStep")
    public ResponseEntity<ResponseMessage> addTaskStep(@PathVariable Long id, @RequestBody TaskStep taskStep){
        int ret = taskService.addTaskStep(id, taskStep);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/removeStep")
    public ResponseEntity<ResponseMessage> removeTaskStep(@PathVariable Long id, @RequestBody TaskStep taskStep){
        int ret = taskService.removeTaskStep(id, taskStep);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Step not in task id: " + id));
        }
    }

    @PutMapping("/tasks/{id}/addRasci")
    public ResponseEntity<ResponseMessage> addTaskRasci(@PathVariable Long id, @RequestBody Rasci rasci){
        int ret = taskService.addRasci(id, rasci);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role already in Task id: " + id));
        }
    }

    @PutMapping("/tasks/{id}/removeRasci")
    public ResponseEntity<ResponseMessage> removeTaskRasci(@PathVariable Long id, @RequestBody Rasci rasci){
        int ret = taskService.removeRasci(id, rasci);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }



    @PutMapping("/tasks/{id}/addGuidance")
    public ResponseEntity<ResponseMessage> addGuidance(@PathVariable Long id, @RequestBody WorkItem workItem){
        int ret = taskService.addGuidanceWorkItem(id, workItem);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Guidance work item added."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Guidance work item already added"));
        }
    }


    @PutMapping("/tasks/{id}/removeGuidance")
    public ResponseEntity<ResponseMessage> removeGuidance(@PathVariable Long id, @RequestBody WorkItem workItem){
        int ret = taskService.removeGuidanceWorkItem(id, workItem);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Guidance work item removed."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Guidance work item not in task id: " + id));
        }
    }

    @PutMapping("/tasks/{id}/addMandatoryInput")
    public ResponseEntity<ResponseMessage> addMandatoryInput(@PathVariable Long id, @RequestBody WorkItem workItem){
        int ret = taskService.addMandatoryInput(id, workItem);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Mandatory input added."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Mandatory input already added"));
        }
    }


    @PutMapping("/tasks/{id}/removeMandatoryInput")
    public ResponseEntity<ResponseMessage> removeMandatoryInput(@PathVariable Long id, @RequestBody WorkItem workItem){
        int ret = taskService.removeMandatoryInput(id, workItem);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Mandatory input removed."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Mandatory input not in task id: " + id));
        }
    }

    @PutMapping("/tasks/{id}/addOptionalInput")
    public ResponseEntity<ResponseMessage> addOptionalInput(@PathVariable Long id, @RequestBody WorkItem workItem){
        int ret = taskService.addOptionalInput(id, workItem);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Optional input added."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Optional input already added"));
        }
    }


    @PutMapping("/tasks/{id}/removeOptionalInput")
    public ResponseEntity<ResponseMessage> removeOptionalInput(@PathVariable Long id, @RequestBody WorkItem workItem){
        int ret = taskService.removeOptionalInput(id, workItem);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Optional input removed."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Optional input not in task id: " + id));
        }
    }

    @PutMapping("/tasks/{id}/addOutput")
    public ResponseEntity<ResponseMessage> addOutput(@PathVariable Long id, @RequestBody WorkItem workItem){
        int ret = taskService.addOutput(id, workItem);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Output added."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Output already added"));
        }
    }


    @PutMapping("/tasks/{id}/removeOutput")
    public ResponseEntity<ResponseMessage> removeOutput(@PathVariable Long id, @RequestBody WorkItem workItem){
        int ret = taskService.removeOptionalInput(id, workItem);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Output removed."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Output not in task id: " + id));
        }
    }
}
