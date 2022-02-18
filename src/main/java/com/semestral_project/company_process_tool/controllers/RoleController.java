package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.services.RoleService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = roleService.getAllRoles();
        if(roles != null){
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/roles/templates")
    public ResponseEntity<List<Role>> getRolesTemplates() {
        List<Role> roles = roleService.getAllTemplates();
        if(roles != null){
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

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
    public ResponseEntity<ResponseMessage> addRole(@RequestBody Role role) {
        boolean ret = roleService.addRole(role);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Role added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role could not be added"));
        }
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<ResponseMessage> updateRole(@PathVariable Long id, @RequestBody Role role){
        int ret = roleService.updateRole(id, role);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Role id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ResponseMessage> removeRole(@PathVariable Long id) {
        boolean ret = roleService.removeRoleById(id);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Role removed."));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role could not be removed."));
        }
    }

}
