package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.TaskStep;
import com.semestral_project.company_process_tool.services.TaskStepService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class TaskStepController {

    @Autowired
    TaskStepService taskStepService;

    @PutMapping("/taskSteps/{id}")
    public ResponseEntity<ResponseMessage> updateStep(@PathVariable Long id, @RequestBody TaskStep step) {
        int ret = taskStepService.updateStep(id, step);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task step id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task step id: " + id + " does not exist"));
        }
    }
}
