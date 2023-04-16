package cz.sli0095.promod.controllers;

import cz.sli0095.promod.entities.*;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.entities.Process;
import cz.sli0095.promod.entities.snapshots.SnapshotItem;
import cz.sli0095.promod.entities.snapshots.SnapshotTask;
import cz.sli0095.promod.services.TaskService;
import cz.sli0095.promod.utils.ResponseMessage;
import cz.sli0095.promod.utils.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class TaskController {

    @Autowired
    TaskService taskService;

    @JsonView(Views.Default.class)
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getTasks() {
        List<Task> tasks = taskService.getAllTasks();
        if(tasks != null){
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/tasks/all")
    public ResponseEntity<List<Task>> getTasksTemplates(@RequestParam long userId, @RequestParam long projectId) {
        List<Task> tasks = taskService.getAllUserCanView(userId, projectId);
        if(tasks != null){
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/tasks/isTemplate")
    public ResponseEntity<List<Task>> getTasksByTemplate(@RequestParam long userId, @RequestParam boolean isTemplate, @RequestParam long projectId) {
        List<Task> tasks = taskService.getAllUserCanViewFiltered(userId, isTemplate, projectId);
        if(tasks != null){
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/tasks/allCanEdit")
    public ResponseEntity<List<Task>> getTasksTemplatesCanEdit(@RequestParam long userId, @RequestParam long projectId) {
        List<Task> tasks = taskService.getAllUserCanEdit(userId, projectId);
        if(tasks != null){
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> taskById(@PathVariable Long id){

        Task task = taskService.getTaskById(id);
        if(task != null){
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/tasks/{id}/addProcess")
    public ResponseEntity<ResponseMessage> addUsableProcess(@PathVariable Long id, @RequestBody Process process, @RequestParam long userId){
        int ret = taskService.addUsableIn(id, userId, process);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Already usable in process"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " could not be updated."));
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/tasks/{id}/usableIn")
    public ResponseEntity<List<Process>> getUsableIn(@PathVariable Long id) {
        List<Process> processes = taskService.getUsableIn(id);
        if (processes != null) {
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/tasks/{id}/removeProcess")
    public ResponseEntity<ResponseMessage> removeUsableTask(@PathVariable Long id, @RequestBody Process process, @RequestParam long userId){
        int ret = taskService.removeUsableIn(id, userId, process);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task  id: " + id + " does not exist"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Not usable in process"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " could not be updated."));
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<ResponseMessage> addTask(@RequestBody Task task, @RequestParam long userId) {
        long ret = taskService.addTask(task, userId);
        if(ret != -1){
            return ResponseEntity.ok(new ResponseMessage("Task added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task could not be added"));
        }
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<ResponseMessage> updateTask(@PathVariable Long id, @RequestBody Task task, @RequestParam long userId){
        int ret = taskService.updateTask(id, task, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/setTemplate")
    public ResponseEntity<ResponseMessage> updateTaskIsTemplate(@PathVariable Long id, @RequestParam boolean isTemplate, @RequestParam long userId){
        int ret = taskService.updateIsTemplate(id, isTemplate, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ResponseMessage> removeTask(@PathVariable Long id, @RequestParam long userId) {
        int ret = taskService.removeTaskById(id, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is deleted"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " could not be deleted"));
        }
    }

    @PutMapping("/tasks/{id}/addStep")
    public ResponseEntity<ResponseMessage> addTaskStep(@PathVariable Long id, @RequestBody TaskStep taskStep, @RequestParam long userId){
        int ret = taskService.addTaskStep(id, taskStep, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/removeStep")
    public ResponseEntity<ResponseMessage> removeTaskStep(@PathVariable Long id, @RequestBody TaskStep taskStep, @RequestParam long userId){
        int ret = taskService.removeTaskStep(id, taskStep, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Step not in task id: " + id));
        }
    }

    @PutMapping("/tasks/{id}/addRasci")
    public ResponseEntity<ResponseMessage> addTaskRasci(@PathVariable Long id, @RequestBody Rasci rasci, @RequestParam long userId){
        int ret = taskService.addRasci(id, rasci, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role already in Task id: " + id));
        }
    }

    @PutMapping("/tasks/{id}/removeRasci")
    public ResponseEntity<ResponseMessage> removeTaskRasci(@PathVariable Long id, @RequestBody Rasci rasci, @RequestParam long userId){
        int ret = taskService.removeRasci(id, rasci, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/addMandatoryInput")
    public ResponseEntity<ResponseMessage> addMandatoryInput(@PathVariable Long id, @RequestBody WorkItem workItem, @RequestParam long userId){
        int ret = taskService.addMandatoryInput(id, workItem, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Mandatory input added."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Mandatory input already added"));
        }
    }

    @PutMapping("/tasks/{id}/removeMandatoryInput")
    public ResponseEntity<ResponseMessage> removeMandatoryInput(@PathVariable Long id, @RequestBody WorkItem workItem, @RequestParam long userId){
        int ret = taskService.removeMandatoryInput(id, workItem, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Mandatory input removed."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Mandatory input not in task id: " + id));
        }
    }

    @PutMapping("/tasks/{id}/addOutput")
    public ResponseEntity<ResponseMessage> addOutput(@PathVariable Long id, @RequestBody WorkItem workItem, @RequestParam long userId){
        int ret = taskService.addOutput(id, workItem, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Output added."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Output already added"));
        }
    }


    @PutMapping("/tasks/{id}/removeOutput")
    public ResponseEntity<ResponseMessage> removeOutput(@PathVariable Long id, @RequestBody WorkItem workItem, @RequestParam long userId){
        int ret = taskService.removeOutput(id, workItem, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " is updated. Output removed."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Output not in task id: " + id));
        }
    }

    @PutMapping("/tasks/{id}/addAccess")
    public ResponseEntity<ResponseMessage> addAccess(@PathVariable Long id, @RequestBody User getAccess, @RequestParam long userId) {

        int status = taskService.addAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access granted."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already has access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/removeAccess")
    public ResponseEntity<ResponseMessage> removeAccess(@PathVariable Long id, @RequestBody User getAccess, @RequestParam long userId) {

        int status = taskService.removeAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/addEdit")
    public ResponseEntity<ResponseMessage> addEdit(@PathVariable Long id, @RequestBody User getEdit, @RequestParam long userId) {

        int status = taskService.addEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing granted."));
        } else if(status == 4){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already can edit."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/{id}/removeEdit")
    public ResponseEntity<ResponseMessage> removeEdit(@PathVariable Long id, @RequestBody User getEdit, @RequestParam long userId) {

        int status = taskService.removeEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have editing rights."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }
    @PutMapping("/tasks/{id}/createSnapshot")
    public ResponseEntity<ResponseMessage> createSnapshot(@PathVariable Long id, @RequestBody SnapshotItem detail, @RequestParam long userId){
        int ret = taskService.createSnapshot(id, userId, detail);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task id: " + id + " created snapshot"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }

    @PutMapping("/tasks/restore")
    public ResponseEntity<ResponseMessage> restoreTask(@RequestBody SnapshotTask snapshot, @RequestParam long userId){
        Task ret = taskService.restoreTask(userId, snapshot);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Task restored, new id is " + ret.getId()));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task not restored"));
        }
    }

    @PutMapping("/tasks/revert")
    public ResponseEntity<ResponseMessage> revertTask(@RequestBody SnapshotTask snapshot, @RequestParam long userId){
        Task ret = taskService.revertTask(userId, snapshot);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Task reverted"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task not reverted"));
        }
    }

    @PutMapping("/tasks/{id}/newConfiguration")
    public ResponseEntity<ResponseMessage> newConfig(@PathVariable Long id, @RequestParam long projectId, @RequestParam long userId){
        Task ret = taskService.createNewConfiguration(userId, id, projectId);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Configuration created, new id is " + ret.getId()));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Configuration not created"));
        }
    }

    @PutMapping("/tasks/{id}/changeOwner")
    public ResponseEntity<ResponseMessage> changeUser(@PathVariable Long id, @RequestParam long userId, @RequestParam long newOwnerId){
        int status = taskService.changeOwner(id, userId, newOwnerId);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Owner changed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User not found."));
        } else if(status == 4){
            return ResponseEntity.badRequest().body(new ResponseMessage("User is not owner."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task id: " + id + " does not exist"));
        }
    }
}
