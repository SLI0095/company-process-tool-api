package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.services.UserService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    UserService userService;

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

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUser();
        if(users != null) {
            return ResponseEntity.ok(users);
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
