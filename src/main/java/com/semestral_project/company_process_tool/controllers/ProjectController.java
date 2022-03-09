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
    public ResponseEntity<List<Project>> getProjects(@RequestParam long userId) {
        List<Project> projects = projectService.getAllProjectsForUser(userId);
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
    public ResponseEntity<ResponseMessage> addProject(@RequestBody Project project, @RequestParam long userId){
        long ret = projectService.addProject(project, userId);
        if(ret != -1){
            return ResponseEntity.ok(new ResponseMessage("Project added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project could not be added."));
        }
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<ResponseMessage> removeProject(@PathVariable Long id, @RequestParam long userId) {
        int ret = projectService.deleteProject(id, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Project id: " + id + " is deleted"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this project."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project could not be deleted."));
        }
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ResponseMessage> updateProject(@PathVariable Long id, @RequestBody Project project, @RequestParam long userId) {
        int ret = projectService.updateProject(id, project, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Project id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this project."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project id: " + id + " does not exist"));
        }
    }

    @GetMapping("/projects/{id}/processes")
    public ResponseEntity<List<Process>> getProcesses(@PathVariable Long id, @RequestParam long userId) {
        List<Process> processes = projectService.getAllProcessesInProjectForUser(id, userId);
        if(processes != null){
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{id}/tasks")
    public ResponseEntity<List<Task>> getTasks(@PathVariable Long id, @RequestParam long userId) {
        List<Task> tasks = projectService.getAllTasksInProjectForUser(id, userId);
        if(tasks != null){
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{id}/roles")
    public ResponseEntity<List<Role>> getRoles(@PathVariable Long id, @RequestParam long userId) {
        List<Role> roles = projectService.getAllRolesInProjectForUser(id, userId);
        if(roles != null){
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{id}/workItems")
    public ResponseEntity<List<WorkItem>> getWorkItems(@PathVariable Long id, @RequestParam long userId) {
        List<WorkItem> workItems = projectService.getAllWorkItemInProjectForUser(id, userId);
        if(workItems != null){
            return ResponseEntity.ok(workItems);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{id}/elements")
    public ResponseEntity<List<Element>> getElements(@PathVariable Long id, @RequestParam long userId) {
        List<Element> elements = projectService.getAllElementsInProjectForUser(id, userId);
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/projects/{id}/addAccess")
    public ResponseEntity<ResponseMessage> addAccess(@PathVariable Long id, @RequestBody User getAccess, @RequestParam long userId) {

        int status = projectService.addAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access granted."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already has access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this project."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project id: " + id + " does not exist"));
        }
    }

    @PutMapping("/projects/{id}/removeAccess")
    public ResponseEntity<ResponseMessage> removeAccess(@PathVariable Long id, @RequestBody User getAccess, @RequestParam long userId) {

        int status = projectService.removeAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this project."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project id: " + id + " does not exist"));
        }
    }

    @PutMapping("/projects/{id}/addEdit")
    public ResponseEntity<ResponseMessage> addEdit(@PathVariable Long id, @RequestBody User getEdit, @RequestParam long userId) {

        int status = projectService.addEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing granted."));
        } else if(status == 4){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already can edit."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this project."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project id: " + id + " does not exist"));
        }
    }

    @PutMapping("/projects/{id}/removeEdit")
    public ResponseEntity<ResponseMessage> removeEdit(@PathVariable Long id, @RequestBody User getEdit, @RequestParam long userId) {

        int status = projectService.removeEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have editing rights."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this project."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project id: " + id + " does not exist"));
        }
    }

//    @PostMapping("/projects/{id}/importTemplate")
//    public ResponseEntity<ResponseMessage> importTemplate(@PathVariable Long id, @RequestBody Process processTemplate, @RequestParam long userId){
//        int ret = projectService.importTemplateProcess(id, processTemplate);
//        if(ret == 1){
//            return ResponseEntity.ok(new ResponseMessage("Process imported"));
//        } else {
//            return ResponseEntity.badRequest().body(new ResponseMessage("Process could not be imported."));
//        }
//    }
}
