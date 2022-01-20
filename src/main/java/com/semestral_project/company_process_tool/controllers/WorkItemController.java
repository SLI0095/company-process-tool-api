package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.WorkItem;
import com.semestral_project.company_process_tool.services.WorkItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
