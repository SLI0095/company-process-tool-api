package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.entities.UserGroup;
import com.semestral_project.company_process_tool.entities.UserType;
import com.semestral_project.company_process_tool.services.UserGroupService;
import com.semestral_project.company_process_tool.services.UserService;
import com.semestral_project.company_process_tool.services.UserTypeService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    UserGroupService userGroupService;
    @Autowired
    UserTypeService userTypeService;

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody User user) {
        long ret = userService.loginUser(user);
        if(ret == 0) {
            return ResponseEntity.badRequest().body(0L);
        } else if (ret == -1){
            return ResponseEntity.badRequest().body(-1L);
        }
        else return ResponseEntity.ok(ret);
    }

    @JsonView(Views.UsersGroups.class)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUser();
        if(users != null) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/userGroups")
    public ResponseEntity<List<UserGroup>> getGroups() {
        List<UserGroup> groups = userGroupService.getAllGroups();
        if(groups != null) {
            return ResponseEntity.ok(groups);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/userGroups")
    public ResponseEntity<ResponseMessage> createGroup(@RequestBody UserGroup group, @RequestParam long userId) {
        long ret = userGroupService.addUserGroup(group,userId);
        if(ret != -1){
            return ResponseEntity.ok(new ResponseMessage("Group created"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Group could not be created"));
        }
    }

    @PutMapping("/userGroups/{id}/addUser")
    public ResponseEntity<ResponseMessage> addUser(@RequestBody User user, @RequestParam long userId, @PathVariable long id) {
        int ret = userGroupService.addUserToGroup(id, user, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("User added to group."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Group id: " + id + " does not exist"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User already in group."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit group."));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody User user){
        int ret = userService.registerUser(user);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("User registered."));
        } else if (ret == 2) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User could not be registered."));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("User with this username already exists."));
        }
    }
}
