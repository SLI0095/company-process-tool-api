package cz.sli0095.promod.controllers;

import cz.sli0095.promod.entities.ProcessMetric;
import cz.sli0095.promod.services.ProcessMetricService;
import cz.sli0095.promod.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
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
