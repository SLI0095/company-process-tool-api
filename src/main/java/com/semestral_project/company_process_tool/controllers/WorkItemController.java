package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.Task;
import com.semestral_project.company_process_tool.entities.WorkItem;
import com.semestral_project.company_process_tool.repositories.WorkItemRepository;
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
    WorkItemRepository workItemRepository;

    @GetMapping("/workItems")
    public ResponseEntity<List<WorkItem>> getTasks() {
        try {
            return org.springframework.http.ResponseEntity.ok((List<WorkItem>) workItemRepository.findAll());
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }
}
