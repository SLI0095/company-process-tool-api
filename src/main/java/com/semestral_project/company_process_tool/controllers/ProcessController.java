package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.Activity;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.ProcessRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProcessController {

    private final ProcessRepository processRepository;

    public ProcessController(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @JsonView(Views.ProcessGeneral.class)
    @GetMapping("/processes")
    public ResponseEntity<List<Process>> getProcesses() {
        try {
            return ResponseEntity.ok((List<Process>)processRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }

    }

    @PostMapping("/processes")
    public ResponseEntity<ResponseMessage> addProcess(@RequestBody Process process){
        try {
            processRepository.save(process);
            return ResponseEntity.ok(new ResponseMessage("Process added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/processes/{id}")
    public ResponseEntity<ResponseMessage> removeProcess(@PathVariable Long id) {
        try {
            processRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @JsonView(Views.ProcessGeneral.class)
    @GetMapping("/processes/{id}")
    public ResponseEntity<Process> processById(@PathVariable Long id) {
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()){
            return ResponseEntity.ok(processData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @JsonView(Views.ProcessRender.class)
    @GetMapping("/processes/{id}/firstActivities")
    public ResponseEntity<List<Activity>> processFirstActivities(@PathVariable Long id) {
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()){
            Process process_ = processData.get();
            List<Activity> activities = process_.getActivities();
            List<Activity> returnValue = new ArrayList<>();
            for(Activity act : activities)
            {
                if(act.getPreviousActivity() == null)
                {
                    returnValue.add(act);
                }
            }
            return ResponseEntity.ok(returnValue);
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PutMapping("/processes/{id}")
    public ResponseEntity<ResponseMessage> updateProcess(@PathVariable Long id, @RequestBody Process process) {
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()){
            Process process_ = processData.get();
            process_.setName(process.getName());
            processRepository.save(process_);
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }
}