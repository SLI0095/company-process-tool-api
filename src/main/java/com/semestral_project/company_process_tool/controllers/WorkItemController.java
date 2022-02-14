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

    @GetMapping("/workItems/{id}")
    public ResponseEntity<WorkItem> workItemById(@PathVariable Long id) {
        WorkItem workItem = workItemService.getWorkItemById(id);
        if(workItem != null) {
            return ResponseEntity.ok(workItem);
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/workItems")
    public ResponseEntity<ResponseMessage> addWorkItem(@RequestBody WorkItem workItem){
        if(workItemService.addWorkItem(workItem)){
            return ResponseEntity.ok(new ResponseMessage("Work item added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item could not be added"));
        }
    }

    @DeleteMapping("/workItems/{id}")
    public ResponseEntity<ResponseMessage> removeWorkItem(@PathVariable Long id) {
        if(workItemService.deleteWorkItem(id)) {
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is deleted"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " could not be deleted"));
        }
    }

    @PutMapping("/workItems/{id}")
    public ResponseEntity<ResponseMessage> updateWorkItem(@PathVariable Long id, @RequestBody WorkItem workItem) {

        int status = workItemService.updateWorkItem(id, workItem);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/addState")
    public ResponseEntity<ResponseMessage> addWorkItemState(@PathVariable Long id, @RequestBody State state){
        int ret = workItemService.addWorkItemState(id, state);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }

    @PutMapping("/workItems/{id}/removeState")
    public ResponseEntity<ResponseMessage> removeWorkItemState(@PathVariable Long id, @RequestBody State state){
        int ret = workItemService.removeWorkItemState(id, state);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("State not in work item id: " + id));
        }
    }

    @PutMapping("/workItems/{id}/addRelation")
    public ResponseEntity<ResponseMessage> addRelation(@PathVariable Long id, @RequestBody WorkItem workItem, @RequestParam String relationType) {

        int status = workItemService.addRelationToWorkItem(id, workItem, relationType);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else if(status == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " already has relation to work item " + workItem.getId()));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Can not add relation to itself"));
        }
    }

    @PutMapping("/workItems/{id}/removeRelation")
    public ResponseEntity<ResponseMessage> removeRelation(@PathVariable Long id, @RequestBody WorkItemRelation workItemRelation) {

        int status = workItemService.removeRelationFromWorkItem(id, workItemRelation);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Work item id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Work item id: " + id + " does not exist"));
        }
    }
}
