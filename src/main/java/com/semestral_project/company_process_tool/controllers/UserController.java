package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @JsonView(Views.Public.class)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        try {
            return ResponseEntity.ok((List<User>) userRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }

    @JsonView(Views.Public.class)
    @GetMapping("/users/{id}")
    public ResponseEntity<User> userById(@PathVariable Long id){
        Optional<User> userData = userRepository.findById(id);
        if(userData.isPresent()) {
            return ResponseEntity.ok(userData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @JsonView(Views.UserRasci.class)
    @GetMapping("/users/{id}/getRasci")
    public ResponseEntity<User> getRasci(@PathVariable Long id){
        Optional<User> userData = userRepository.findById(id);
        if(userData.isPresent()) {
            return ResponseEntity.ok(userData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ResponseMessage> updateUser(@PathVariable Long id, @RequestBody User user){
        Optional<User> userData = userRepository.findById(id);
        if(userData.isPresent()) {
            User user_ = userData.get();
            user_.setName(user.getName());
            user_.setSurname(user.getSurname());
            user_.setPosition(user.getPosition());

            userRepository.save(user_);
            return ResponseEntity.ok(new ResponseMessage("User id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("User id: " + id + " does not exist"));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseMessage> addUser(@RequestBody User user) {
        try {
            userRepository.save(user);
            return ResponseEntity.ok(new ResponseMessage("User added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<ResponseMessage> removeUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("User id: " + id + " is deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

}
