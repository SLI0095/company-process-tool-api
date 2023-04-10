package cz.sli0095.promod.controllers;

import cz.sli0095.promod.entities.TaskStep;
import cz.sli0095.promod.utils.ResponseMessage;
import cz.sli0095.promod.services.TaskStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class TaskStepController {

    @Autowired
    TaskStepService taskStepService;

    @PutMapping("/taskSteps/{id}")
    public ResponseEntity<ResponseMessage> updateStep(@PathVariable Long id, @RequestBody TaskStep step, @RequestParam long userId) {
        int ret = taskStepService.updateStep(id, step, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Task step id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this task."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Task step id: " + id + " does not exist"));
        }
    }
}
