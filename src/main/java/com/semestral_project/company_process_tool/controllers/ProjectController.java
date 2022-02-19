package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.services.ProjectService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getProjects() {
        List<Project> projects = projectService.getAllProjects();
        if(projects != null){
            return ResponseEntity.ok(projects);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> projectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        if(project != null){
            return ResponseEntity.ok(project);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/projects")
    public ResponseEntity<ResponseMessage> addProject(@RequestBody Project project){
        boolean ret = projectService.addProject(project);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Project added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project could not be added."));
        }
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<ResponseMessage> removeProcess(@PathVariable Long id) {
        boolean ret = projectService.deleteProject(id);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Project id: " + id + " is deleted"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project could not be deleted."));
        }
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ResponseMessage> updateProcess(@PathVariable Long id, @RequestBody Project project) {
        int ret = projectService.updateProject(id, project);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Project id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project id: " + id + " does not exist"));
        }
    }

    @GetMapping("/projects/{id}/processes")
    public ResponseEntity<List<Process>> getProcesses(@PathVariable Long id) {
        List<Process> processes = projectService.getAllProcessesInProject(id);
        if(processes != null){
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{id}/tasks")
    public ResponseEntity<List<Task>> getTasks(@PathVariable Long id) {
        List<Task> tasks = projectService.getAllTasksInProject(id);
        if(tasks != null){
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{id}/roles")
    public ResponseEntity<List<Role>> getRoles(@PathVariable Long id) {
        List<Role> roles = projectService.getAllRolesInProject(id);
        if(roles != null){
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{id}/workItems")
    public ResponseEntity<List<WorkItem>> getWorkItems(@PathVariable Long id) {
        List<WorkItem> workItems = projectService.getAllWorkItemInProject(id);
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{id}/elements")
    public ResponseEntity<List<Element>> getElements(@PathVariable Long id) {
        List<Element> elements = projectService.getAllElementsInProject(id);
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
