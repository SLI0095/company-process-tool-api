package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.services.WorkItemService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class WorkItemController {

    @Autowired
    WorkItemService workItemService;


    @GetMapping("/workItems")
    public ResponseEntity<List<WorkItem>> getWorkItems() {
        List<WorkItem> workItems = workItemService.getAllWorkItems();
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/workItems/templates")
    public ResponseEntity<List<WorkItem>> getWorkItemsTemplates(@RequestParam long userId) {
        List<WorkItem> workItems = workItemService.getAllTemplates(userId);
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/workItems/templatesCanEdit")
    public ResponseEntity<List<WorkItem>> getWorkItemsTemplatesCanEdit(@RequestParam long userId) {
        List<WorkItem> workItems = workItemService.getAllTemplatesCanEdit(userId);
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

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

    @PutMapping("/workItems/{id}/addRelation")
    public ResponseEntity<ResponseMessage> addRelation(@PathVariable Long id, @RequestBody WorkItem workItem, @RequestParam String relationType, @RequestParam long userId) {

        int status = workItemService.addRelationToWorkItem(id, workItem, relationType, userId);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else if(status == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else if(status == 4){
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " already has relation to work item " + workItem.getId()));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Can not add relation to itself"));
        }
    }

    @PutMapping("/workItems/{id}/removeRelation")
    public ResponseEntity<ResponseMessage> removeRelation(@PathVariable Long id, @RequestBody WorkItemRelation workItemRelation, @RequestParam long userId) {

        int status = workItemService.removeRelationFromWorkItem(id, workItemRelation, userId);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this work item."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/addAccess")
    public ResponseEntity<ResponseMessage> addAccess(@PathVariable Long id, @RequestBody User getAccess, @RequestParam long userId) {

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
    public ResponseEntity<ResponseMessage> removeAccess(@PathVariable Long id, @RequestBody User getAccess, @RequestParam long userId) {

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
    public ResponseEntity<ResponseMessage> addEdit(@PathVariable Long id, @RequestBody User getEdit, @RequestParam long userId) {

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
    public ResponseEntity<ResponseMessage> removeEdit(@PathVariable Long id, @RequestBody User getEdit, @RequestParam long userId) {

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
}
