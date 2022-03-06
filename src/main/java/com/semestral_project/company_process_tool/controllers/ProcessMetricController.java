package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.ProcessMetric;
import com.semestral_project.company_process_tool.services.ProcessMetricService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProcessMetricController {

    @Autowired
    ProcessMetricService metricService;

    @PutMapping("/metrics/{id}")
    public ResponseEntity<ResponseMessage> updateMetric(@PathVariable Long id, @RequestBody ProcessMetric metric, @RequestParam long userId) {
        int ret = metricService.updateMetric(id, metric, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Metric id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Metric id: " + id + " does not exist"));
        }
    }
}
