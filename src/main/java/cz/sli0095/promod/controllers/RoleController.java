package cz.sli0095.promod.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.entities.Role;
import cz.sli0095.promod.entities.Task;
import cz.sli0095.promod.entities.UserType;
import cz.sli0095.promod.entities.snapshots.SnapshotItem;
import cz.sli0095.promod.entities.snapshots.SnapshotRole;
import cz.sli0095.promod.services.RoleService;
import cz.sli0095.promod.utils.ResponseMessage;
import cz.sli0095.promod.utils.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class RoleController {

    @Autowired
    RoleService roleService;

    @JsonView(Views.Default.class)
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = roleService.getAllRoles();
        if(roles != null){
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/roles/all")
    public ResponseEntity<List<Role>> getRolesTemplates(@RequestParam long userId, @RequestParam long projectId) {
        List<Role> roles = roleService.getAllUserCanView(userId, projectId);
        if(roles != null){
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/roles/isTemplate")
    public ResponseEntity<List<Role>> getRolesByTemplates(@RequestParam long userId, @RequestParam boolean isTemplate, @RequestParam long projectId) {
        List<Role> roles = roleService.getAllUserCanViewByTemplate(userId, isTemplate, projectId);
        if(roles != null){
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/roles/allCanEdit")
    public ResponseEntity<List<Role>> getRolesTemplatesCanEdit(@RequestParam long userId, @RequestParam long projectId) {
        List<Role> roles = roleService.getAllUserCanEdit(userId, projectId);
        if(roles != null){
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/roles/forTask")
    public ResponseEntity<List<Role>> getRolesForTask(@RequestParam long userId, @RequestParam long taskId, @RequestParam long projectId) {
        List<Role> roles = roleService.getUsableInForUser(userId, taskId, projectId);
        if(roles != null){
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/roles/{id}/addTask")
    public ResponseEntity<ResponseMessage> addUsableTask(@PathVariable Long id, @RequestBody Task task, @RequestParam long userId){
        int ret = roleService.addUsableIn(id, userId, task);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Role id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Already usable in task"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " could not be updated."));
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/roles/{id}/usableIn")
    public ResponseEntity<List<Task>> getUsableIn(@PathVariable Long id){
        List<Task> tasks = roleService.getUsableIn(id);
        if(tasks != null){
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/roles/{id}/removeTask")
    public ResponseEntity<ResponseMessage> removeUsableTask(@PathVariable Long id, @RequestBody Task task, @RequestParam long userId){
        int ret = roleService.removeUsableIn(id, userId, task);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Role id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Not usable in task"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " could not be updated."));
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> roleById(@PathVariable Long id){
        Role role = roleService.getRoleById(id);
        if(role != null){
            return ResponseEntity.ok(role);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/roles")
    public ResponseEntity<ResponseMessage> addRole(@RequestBody Role role, @RequestParam long userId) {
        long ret = roleService.addRole(role, userId);
        if(ret != -1){
            return ResponseEntity.ok(new ResponseMessage("Role added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role could not be added"));
        }
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<ResponseMessage> updateRole(@PathVariable Long id, @RequestBody Role role, @RequestParam long userId){
        int ret = roleService.updateRole(id, role, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Role id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

    @PutMapping("/roles/{id}/setTemplate")
    public ResponseEntity<ResponseMessage> updateRoleTemplate(@PathVariable Long id, @RequestParam boolean isTemplate, @RequestParam long userId){
        int ret = roleService.updateIsTemplate(id, isTemplate, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Role id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ResponseMessage> removeRole(@PathVariable Long id, @RequestParam long userId) {
        int ret = roleService.removeRoleById(id, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Role removed."));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role could not be removed."));
        }
    }

    @PutMapping("/roles/{id}/addAccess")
    public ResponseEntity<ResponseMessage> addAccess(@PathVariable Long id, @RequestBody UserType getAccess, @RequestParam long userId) {

        int status = roleService.addAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access granted."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already has access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

    @PutMapping("/roles/{id}/removeAccess")
    public ResponseEntity<ResponseMessage> removeAccess(@PathVariable Long id, @RequestBody UserType getAccess, @RequestParam long userId) {

        int status = roleService.removeAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

    @PutMapping("/roles/{id}/addEdit")
    public ResponseEntity<ResponseMessage> addEdit(@PathVariable Long id, @RequestBody UserType getEdit, @RequestParam long userId) {

        int status = roleService.addEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing granted."));
        } else if(status == 4){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already can edit."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

    @PutMapping("/roles/{id}/removeEdit")
    public ResponseEntity<ResponseMessage> removeEdit(@PathVariable Long id, @RequestBody UserType getEdit, @RequestParam long userId) {

        int status = roleService.removeEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have editing rights."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

    @PutMapping("/roles/{id}/createSnapshot")
    public ResponseEntity<ResponseMessage> createSnaphsot(@PathVariable Long id, @RequestBody SnapshotItem detail, @RequestParam long userId){
        int ret = roleService.createSnapshot(id, userId, detail);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Role id: " + id + " created snapshot"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this role."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

    @PutMapping("/roles/restore")
    public ResponseEntity<ResponseMessage> restoreRole(@RequestBody SnapshotRole snapshot, @RequestParam long userId){
        Role ret = roleService.restoreRole(userId, snapshot);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Role restored, new id is " + ret.getId()));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role not restored"));
        }
    }

    @PutMapping("/roles/revert")
    public ResponseEntity<ResponseMessage> revertRole(@RequestBody SnapshotRole snapshot, @RequestParam long userId){
        Role ret = roleService.revertRole(userId, snapshot);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Role reverted"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role not reverted"));
        }
    }

    @PutMapping("/roles/{id}/newConfiguration")
    public ResponseEntity<ResponseMessage> newConfig(@PathVariable Long id, @RequestParam long projectId, @RequestParam long userId){
        Role ret = roleService.createNewConfiguration(userId, id, projectId);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Configuration created, new id is " + ret.getId()));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Configuration not created"));
        }
    }

    @PutMapping("/roles/{id}/changeOwner")
    public ResponseEntity<ResponseMessage> changeUser(@PathVariable Long id, @RequestParam long userId, @RequestParam long newOwnerId){
        int status = roleService.changeOwner(id, userId, newOwnerId);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Owner changed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User not found."));
        } else if(status == 4){
            return ResponseEntity.badRequest().body(new ResponseMessage("User is not owner."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

}
