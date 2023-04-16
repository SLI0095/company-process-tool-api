package cz.sli0095.promod.controllers;

import cz.sli0095.promod.entities.Process;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.entities.snapshots.SnapshotItem;
import cz.sli0095.promod.entities.snapshots.SnapshotWorkItem;
import cz.sli0095.promod.services.WorkItemService;
import cz.sli0095.promod.utils.ResponseMessage;
import cz.sli0095.promod.utils.Views;
import cz.sli0095.promod.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class WorkItemController {

    @Autowired
    WorkItemService workItemService;


    @JsonView(Views.Default.class)
    @GetMapping("/workItems")
    public ResponseEntity<List<WorkItem>> getWorkItems() {
        List<WorkItem> workItems = workItemService.getAllWorkItems();
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/workItems/all")
    public ResponseEntity<List<WorkItem>> getWorkItemsTemplates(@RequestParam long userId, @RequestParam long projectId) {
        List<WorkItem> workItems = workItemService.getAllUserCanView(userId, projectId);
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/workItems/isTemplate")
    public ResponseEntity<List<WorkItem>> getWorkItemsIsTemplate(@RequestParam long userId, @RequestParam boolean isTemplate, @RequestParam long projectId) {
        List<WorkItem> workItems = workItemService.getAllUserCanViewByTemplate(userId, isTemplate, projectId);
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/workItems/allCanEdit")
    public ResponseEntity<List<WorkItem>> getWorkItemsTemplatesCanEdit(@RequestParam long userId, @RequestParam long projectId) {
        List<WorkItem> workItems = workItemService.getAllUserCanEdit(userId, projectId);
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/workItems/forProcess")
    public ResponseEntity<List<WorkItem>> getWorkItemsForProcess(@RequestParam Long userId, @RequestParam Long processId, @RequestParam long projectId) {
        List<WorkItem> workItems = workItemService.getUsableInProcessForUser(userId, processId, projectId);
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/workItems/forTask")
    public ResponseEntity<List<WorkItem>> getWorkItemsForTask(@RequestParam Long userId, @RequestParam Long taskId, @RequestParam long projectId) {
        List<WorkItem> workItems = workItemService.getUsableInTaskForUser(userId, taskId, projectId);
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/workItems/{id}/usableInProcesses")
    public ResponseEntity<List<Process>> getUsableInProcesses(@PathVariable Long id) {
        List<Process> processes = workItemService.getUsableInProcesses(id);
        if (processes != null) {
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/workItems/{id}/usableInTasks")
    public ResponseEntity<List<Task>> getUsableInTasks(@PathVariable Long id) {
        List<Task> tasks = workItemService.getUsableInTasks(id);
        if (tasks != null) {
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/workItems/{id}/addElement")
    public ResponseEntity<ResponseMessage> addUsableTask(@PathVariable Long id, @RequestBody Element element, @RequestParam long userId){
        int ret = workItemService.addUsableIn(id, userId, element);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Already usable in element"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " could not be updated."));
        }
    }

    @PutMapping("/workItems/{id}/removeElement")
    public ResponseEntity<ResponseMessage> removeUsableTask(@PathVariable Long id, @RequestBody Element element, @RequestParam long userId){
        int ret = workItemService.removeUsableIn(id, userId, element);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Not usable in element"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " could not be updated."));
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/workItems/{id}")
    public ResponseEntity<WorkItem> workItemById(@PathVariable Long id) {
        WorkItem workItem = workItemService.getWorkItemById(id);
        if(workItem != null) {
            return ResponseEntity.ok(workItem);
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/workItems")
    public ResponseEntity<ResponseMessage> addWorkItem(@RequestBody WorkItem workItem, @RequestParam long userId){
        if(workItemService.addWorkItem(workItem, userId) != -1){
            return ResponseEntity.ok(new ResponseMessage("Work item added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item could not be added"));
        }
    }

    @DeleteMapping("/workItems/{id}")
    public ResponseEntity<ResponseMessage> removeWorkItem(@PathVariable Long id, @RequestParam long userId) {
        if(workItemService.deleteWorkItem(id, userId)) {
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is deleted"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " could not be deleted"));
        }
    }

    @PutMapping("/workItems/{id}")
    public ResponseEntity<ResponseMessage> updateWorkItem(@PathVariable Long id, @RequestBody WorkItem workItem, @RequestParam long userId) {

        int status = workItemService.updateWorkItem(id, workItem, userId);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        }else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/setTemplate")
    public ResponseEntity<ResponseMessage> updateWorkItemIsTemplate(@PathVariable Long id, @RequestParam boolean isTemplate, @RequestParam long userId) {

        int status = workItemService.updateIsTemplate(id, isTemplate, userId);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        }else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/addState")
    public ResponseEntity<ResponseMessage> addWorkItemState(@PathVariable Long id, @RequestBody State state, @RequestParam long userId){
        int ret = workItemService.addWorkItemState(id, state, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/removeState")
    public ResponseEntity<ResponseMessage> removeWorkItemState(@PathVariable Long id, @RequestBody State state, @RequestParam long userId){
        int ret = workItemService.removeWorkItemState(id, state, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("State not in work item id: " + id));
        }
    }

    @PutMapping("/workItems/{id}/addAccess")
    public ResponseEntity<ResponseMessage> addAccess(@PathVariable Long id, @RequestBody UserType getAccess, @RequestParam long userId) {

        int status = workItemService.addAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access granted."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already has access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/removeAccess")
    public ResponseEntity<ResponseMessage> removeAccess(@PathVariable Long id, @RequestBody UserType getAccess, @RequestParam long userId) {

        int status = workItemService.removeAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/addEdit")
    public ResponseEntity<ResponseMessage> addEdit(@PathVariable Long id, @RequestBody UserType getEdit, @RequestParam long userId) {

        int status = workItemService.addEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing granted."));
        } else if(status == 4){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already can edit."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/removeEdit")
    public ResponseEntity<ResponseMessage> removeEdit(@PathVariable Long id, @RequestBody UserType getEdit, @RequestParam long userId) {

        int status = workItemService.removeEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have editing rights."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/createSnapshot")
    public ResponseEntity<ResponseMessage> createSnapshot(@PathVariable Long id, @RequestBody SnapshotItem detail, @RequestParam long userId){
        int ret = workItemService.createSnapshot(id, userId, detail);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " created snapshot"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/restore")
    public ResponseEntity<ResponseMessage> restoreWorkItem(@RequestBody SnapshotWorkItem snapshot, @RequestParam long userId){
        WorkItem ret = workItemService.restoreWorkItem(userId, snapshot);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Work item restored, new id is " + ret.getId()));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item not restored"));
        }
    }

    @PutMapping("/workItems/revert")
    public ResponseEntity<ResponseMessage> revertWorkItem(@RequestBody SnapshotWorkItem snapshot, @RequestParam long userId){
        WorkItem ret = workItemService.revertWorkItem(userId, snapshot);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Work item reverted"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item not reverted"));
        }
    }

    @PutMapping("/workItems/{id}/newConfiguration")
    public ResponseEntity<ResponseMessage> newConfig(@PathVariable Long id, @RequestParam long projectId, @RequestParam long userId){
        WorkItem ret = workItemService.createNewConfiguration(userId, id, projectId);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Configuration created, new id is " + ret.getId()));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Configuration not created"));
        }
    }

    @PutMapping("/workItems/{id}/changeOwner")
    public ResponseEntity<ResponseMessage> changeUser(@PathVariable Long id, @RequestParam long userId, @RequestParam long newOwnerId){
        int status = workItemService.changeOwner(id, userId, newOwnerId);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Owner changed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User not found."));
        } else if(status == 4){
            return ResponseEntity.badRequest().body(new ResponseMessage("User is not owner."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }
}
