package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.State;
import com.semestral_project.company_process_tool.entities.TaskStep;
import com.semestral_project.company_process_tool.repositories.TaskStepRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class TaskStepController {

    @Autowired
    TaskStepRepository taskStepRepository;

    @PutMapping("/taskSteps/{id}")
    public ResponseEntity<ResponseMessage> updateStep(@PathVariable Long id, @RequestBody TaskStep step) {
        Optional<TaskStep> stepData = taskStepRepository.findById(id);

        if(stepData.isPresent()){
            TaskStep step_ = stepData.get();
            step_.setName(step.getName());
            step_.setDescription(step.getDescription());

            taskStepRepository.save(step_);
            return ResponseEntity.ok(new ResponseMessage("Task step id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task step id: " + id + " does not exist"));
        }
    }
}
