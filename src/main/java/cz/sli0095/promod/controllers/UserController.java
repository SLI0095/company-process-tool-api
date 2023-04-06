package cz.sli0095.promod.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.entities.UserGroup;
import cz.sli0095.promod.entities.UserType;
import cz.sli0095.promod.services.UserGroupService;
import cz.sli0095.promod.services.UserService;
import cz.sli0095.promod.services.UserTypeService;
import cz.sli0095.promod.utils.ResponseMessage;
import cz.sli0095.promod.utils.Views;
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
    @JsonView(Views.UsersGroups.class)
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        User user = userService.getUserById(id);
        if(user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @JsonView(Views.Projects.class)
    @GetMapping("/users/{id}/projects")
    public ResponseEntity<User> getUserWithProjects(@PathVariable long id) {
        User user = userService.getUserByIdWithProjects(id);
        if(user != null) {
            return ResponseEntity.ok(user);
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

    @JsonView(Views.Default.class)
    @GetMapping("/userGroups/{id}")
    public ResponseEntity<UserGroup> getGroup(@PathVariable long id) {
        UserGroup group = userGroupService.getGroupById(id);
        if(group != null) {
            return ResponseEntity.ok(group);
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

    @PutMapping("/userGroups/{id}/removeUser")
    public ResponseEntity<ResponseMessage> removeUser(@RequestBody User user, @RequestParam long userId, @PathVariable long id) {
        int ret = userGroupService.removeUserFromGroup(id, user, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("User removed from group."));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Group id: " + id + " does not exist"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User not in group."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit group."));
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/userTypes")
    public ResponseEntity<List<UserType>> getAll() {
        List<UserType> all = userTypeService.getAllUsersAndGroups();
        if(all != null) {
            return ResponseEntity.ok(all);
        } else {
            return ResponseEntity.badRequest().body(null);
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
