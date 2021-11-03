package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.Position;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.PositionRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PositionController {

    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    public PositionController(PositionRepository positionRepository, UserRepository userRepository) {
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
    }

    @JsonView(Views.Minimal.class)
    @GetMapping("/positions")
    public ResponseEntity<List<Position>> getPositions(){
        try {
            return ResponseEntity.ok((List<Position>) positionRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }


    @PostMapping("/positions")
    public ResponseEntity<ResponseMessage> addPosition(@RequestBody Position position){
        try {
            positionRepository.save(position);
            return ResponseEntity.ok(new ResponseMessage("Position added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/positions/{id}")
    public ResponseEntity<ResponseMessage> removePosition(@PathVariable Long id) {
        try {
            positionRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Activity id: " + id + " is deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @JsonView(Views.Minimal.class)
    @GetMapping("/positions/{id}")
    public ResponseEntity<Position> positionById(@PathVariable Long id) {
        Optional<Position> positionData = positionRepository.findById(id);
        if(positionData.isPresent()){
            return ResponseEntity.ok(positionData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @JsonView(Views.PositionUsers.class)
    @GetMapping("/positions/{id}/allUsers")
    public ResponseEntity<Position> getPositionUsers(@PathVariable Long id) {
        Optional<Position> positionData = positionRepository.findById(id);
        if(positionData.isPresent()){
            return ResponseEntity.ok(positionData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }


    @PutMapping("/positions/{id}")
    public ResponseEntity<ResponseMessage> updatePosition(@PathVariable Long id, @RequestBody Position position) {
        Optional<Position> positionData = positionRepository.findById(id);
        if(positionData.isPresent()){
            Position position_ = positionData.get();
            position_.setName(position.getName());
            positionRepository.save(position_);
            return ResponseEntity.ok(new ResponseMessage("Position id: " + id + " is updated"));
        }
        else return ResponseEntity.badRequest().body(new ResponseMessage("Position id: " + id + " does not exist"));
    }

    @PutMapping("/positions/{positionId}/addUser")
    public ResponseEntity<ResponseMessage> setPosition(@RequestBody User user, @PathVariable Long positionId) {
        Optional<Position> positionData = positionRepository.findById(positionId);

        if(positionData.isPresent())
        {
            Position position_ = positionData.get();
            Optional<User> userData = userRepository.findById(user.getId());
            if(userData.isPresent())
            {
                User user_ = userData.get();
                position_.addUser(user_);
                positionRepository.save(position_);
                return ResponseEntity.ok(new ResponseMessage("Position id: " + positionId + " is added for User id: " + user.getId()));
            }
            else
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("User id: " + user.getId() + " does not exist"));
            }
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Position id: " + positionId + " does not exist"));
        }
    }
}
