package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.Project;
import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.entities.UserType;
import com.semestral_project.company_process_tool.services.ProjectService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @JsonView(Views.Default.class)
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        if(projects != null){
            return ResponseEntity.ok(projects);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/projects/all")
    public ResponseEntity<List<Project>> getProjects(@RequestParam long userId) {
        List<Project> projects = projectService.getAllUserCanView(userId );
        if(projects != null){
            return ResponseEntity.ok(projects);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/projects")
    public ResponseEntity<ResponseMessage> addProject(@RequestBody Project project, @RequestParam long userId) {
        long ret = projectService.addProject(project, userId);
        if(ret != -1){
            return ResponseEntity.ok(new ResponseMessage("Project added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project could not be added"));
        }
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ResponseMessage> updateProject(@PathVariable Long id, @RequestBody Project project, @RequestParam long userId){
        int ret = projectService.updateProject(id, project, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Role id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<ResponseMessage> removeRole(@PathVariable Long id, @RequestParam long userId) {
        int ret = projectService.removeProject(id, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Role removed."));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role could not be removed."));
        }
    }

    @PutMapping("/projects/{id}/addAccess")
    public ResponseEntity<ResponseMessage> addAccess(@PathVariable Long id, @RequestBody UserType getAccess, @RequestParam long userId) {

        int status = projectService.addAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access granted."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already has access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this project."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project id: " + id + " does not exist"));
        }
    }

    @PutMapping("/projects/{id}/removeAccess")
    public ResponseEntity<ResponseMessage> removeAccess(@PathVariable Long id, @RequestBody UserType getAccess, @RequestParam long userId) {

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
    public ResponseEntity<ResponseMessage> addEdit(@PathVariable Long id, @RequestBody UserType getEdit, @RequestParam long userId) {

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
    public ResponseEntity<ResponseMessage> removeEdit(@PathVariable Long id, @RequestBody UserType getEdit, @RequestParam long userId) {

        int status = projectService.removeEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have editing rights."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this project."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project id: " + id + " does not exist"));
        }
    }

}
