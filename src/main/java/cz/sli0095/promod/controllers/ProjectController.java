package cz.sli0095.promod.controllers;

import cz.sli0095.promod.entities.Project;
import cz.sli0095.promod.entities.UserType;
import cz.sli0095.promod.services.ProjectService;
import cz.sli0095.promod.utils.ResponseMessage;
import cz.sli0095.promod.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @JsonView(Views.Basic.class)
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        if(projects != null){
            return ResponseEntity.ok(projects);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Basic.class)
    @GetMapping("/projects/all")
    public ResponseEntity<List<Project>> getProjects(@RequestParam long userId) {
        List<Project> projects = projectService.getAllUserCanView(userId );
        if(projects != null){
            return ResponseEntity.ok(projects);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Basic.class)
    @GetMapping("/projects/canEdit")
    public ResponseEntity<List<Project>> getProjectsCanEdit(@RequestParam long userId) {
        List<Project> projects = projectService.getAllUserCanEdit(userId );
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

    @JsonView(Views.Default.class)
    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        if(project != null){
            return ResponseEntity.ok(project);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ResponseMessage> updateProject(@PathVariable Long id, @RequestBody Project project, @RequestParam long userId){
        int ret = projectService.updateProject(id, project, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Project id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this project."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<ResponseMessage> removeRole(@PathVariable Long id, @RequestParam long userId) {
        int ret = projectService.removeProject(id, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Project removed."));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot delete this project."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Project could not be removed."));
        }
    }

    @JsonView(Views.Default.class)
    @PutMapping("/projects/{id}/canAccess")
    public ResponseEntity<ResponseMessage> canAccess(@PathVariable Long id,  @RequestParam long userId) {
        boolean ret = projectService.canAccessProject(id, userId);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Can access"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Cannot access"));
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
