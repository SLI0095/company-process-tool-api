package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.RoleRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class RoleController {

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRoles() {
        try {
            return org.springframework.http.ResponseEntity.ok((List<Role>) roleRepository.findAll());
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> roleById(@PathVariable Long id){
        Optional<Role> roleData = roleRepository.findById(id);
        if(roleData.isPresent()) {
            return ResponseEntity.ok(roleData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/roles")
    public ResponseEntity<ResponseMessage> addRole(@RequestBody Role role) {
        try {
            roleRepository.save(role);
            return ResponseEntity.ok(new ResponseMessage("Role added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role could not be added"));
        }
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<ResponseMessage> updateRole(@PathVariable Long id, @RequestBody Role role){
        Optional<Role> roleData = roleRepository.findById(id);
        if(roleData.isPresent()) {
            Role role_ = roleData.get();
            role_ = fillRole(role_, role);

            roleRepository.save(role_);
            return ResponseEntity.ok(new ResponseMessage("Role id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Role id: " + id + " does not exist"));
        }
    }


    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ResponseMessage> removeRole(@PathVariable Long id) {
        try {
            roleRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Role id: " + id + " is deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    private Role fillRole(Role oldRole, Role updateRole){
        oldRole.setName(updateRole.getName());
        oldRole.setBriefDescription(updateRole.getBriefDescription());
        oldRole.setMainDescription(updateRole.getMainDescription());
        oldRole.setVersion(updateRole.getVersion());
        oldRole.setChangeDate(updateRole.getChangeDate());
        oldRole.setChangeDescription(updateRole.getChangeDescription());
        oldRole.setSkills(updateRole.getSkills());
        oldRole.setAssignmentApproaches(updateRole.getAssignmentApproaches());
        return oldRole;
    }
}
