package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotProcess;
import com.semestral_project.company_process_tool.services.ProcessService;
import com.semestral_project.company_process_tool.services.RasciMatrixService;
import com.semestral_project.company_process_tool.utils.ProcessAndBpmnHolder;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProcessController {

    @Autowired
    ProcessService processService;
    @Autowired
    RasciMatrixService rasciMatrixService;

    @JsonView(Views.Default.class)
    @GetMapping("/processes")
    public ResponseEntity<List<Process>> getProcesses() {
        List<Process> processes = processService.getAllProcesses();
        if(processes != null){
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/processes/all")
    public ResponseEntity<List<Process>> getProcessesTemplates(@RequestParam long userId, @RequestParam long projectId) {
        List<Process> processes = processService.getAllUserCanView(userId, projectId);
        if(processes != null){
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/processes/isTemplate")
    public ResponseEntity<List<Process>> getProcessesByTemplate(@RequestParam long userId, @RequestParam boolean isTemplate, @RequestParam long projectId) {
        List<Process> processes = processService.getAllUserCanViewByTemplate(userId, isTemplate, projectId);
        if(processes != null){
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/processes/allCanEdit")
    public ResponseEntity<List<Process>> getProcessesTemplatesCanEdit(@RequestParam long userId, @RequestParam long projectId) {
        List<Process> processes = processService.getAllUserCanEdit(userId, projectId);
        if(processes != null){
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/processes/{id}")
    public ResponseEntity<Process> processById(@PathVariable Long id) {
        Process process = processService.getProcessById(id);
        if(process != null){
            return ResponseEntity.ok(process);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/processes/{id}/usableIn")
    public ResponseEntity<List<Process>> getUsableIn(@PathVariable Long id) {
        List<Process> processes = processService.getUsableIn(id);
        if (processes != null) {
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/processes/{id}/addMetric")
    public ResponseEntity<ResponseMessage> addTaskStep(@PathVariable Long id, @RequestParam long userId, @RequestBody ProcessMetric metric){
        int ret = processService.addMetric(id, metric, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @PutMapping("/processes/{id}/removeMetric")
    public ResponseEntity<ResponseMessage> removeTaskStep(@PathVariable Long id, @RequestParam long userId, @RequestBody ProcessMetric metric){
        int ret = processService.removeMetric(id, metric, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Metric not in process id: " + id));
        }
    }

    @PutMapping("/processes/{id}/addProcess")
    public ResponseEntity<ResponseMessage> addUsableProcess(@PathVariable Long id, @RequestBody Process process, @RequestParam long userId){
        int ret = processService.addUsableIn(id, userId, process);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Already usable in process"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " could not be updated."));
        }
    }

    @PutMapping("/processes/{id}/removeProcess")
    public ResponseEntity<ResponseMessage> removeUsableProcess(@PathVariable Long id, @RequestBody Process process, @RequestParam long userId){
        int ret = processService.removeUsableIn(id, userId, process);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
        } else if(ret == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        } else if(ret == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Not usable in process"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " could not be updated."));
        }
    }

    @PostMapping("/processes")
    public ResponseEntity<ResponseMessage> addProcess(@RequestBody Process process, @RequestParam long userId){
        long ret = processService.addProcess(process, userId);
        if(ret != -1){
            return ResponseEntity.ok(new ResponseMessage("Process added"));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process could not be added."));
        }
    }

    @DeleteMapping("/processes/{id}")
    public ResponseEntity<ResponseMessage> removeProcess(@PathVariable Long id, @RequestParam long userId) {
        int ret = processService.deleteProcessById(id, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is deleted"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process could not be deleted."));
        }
    }

    @PutMapping("/processes/{id}")
    public ResponseEntity<ResponseMessage> updateProcess(@PathVariable Long id, @RequestBody Process process, @RequestParam long userId) {
        int ret = processService.updateProcess(id, process, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @PutMapping("/processes/{id}/setTemplate")
    public ResponseEntity<ResponseMessage> updateProcessIsTemplate(@PathVariable Long id, @RequestParam boolean isTemplate, @RequestParam long userId) {
        int ret = processService.updateIsTemplate(id, isTemplate, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }


    @PutMapping("/processes/{id}/saveBPMN")
    public ResponseEntity<ResponseMessage> saveBPMN(@PathVariable Long id, @RequestBody BPMNfile bpmn, @RequestParam long userId){
        int ret = processService.saveWorkflow(id, bpmn, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated. Workflow saved."));
        }else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/processes/{id}/rasci")
    public ResponseEntity<String[][]> processRasci(@PathVariable Long id) {
        Process process = processService.getProcessById(id);
        if(process != null){
            String[][] matrix = rasciMatrixService.getMatrixForRender(process);
            return ResponseEntity.ok(matrix);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/processes/fromBpmn")
    public ResponseEntity<ResponseMessage> addProcesFromBPMN(@RequestBody ProcessAndBpmnHolder holder, @RequestParam long userId){
        boolean ret = processService.addProcessFromFile(holder,userId);
        if(ret){
            return ResponseEntity.ok(new ResponseMessage("Process added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process could not be added."));
        }
    }

    @PutMapping("/processes/{id}/addAccess")
    public ResponseEntity<ResponseMessage> addAccess(@PathVariable Long id, @RequestBody UserType getAccess, @RequestParam long userId) {

        int status = processService.addAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access granted."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already has access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @PutMapping("/processes/{id}/removeAccess")
    public ResponseEntity<ResponseMessage> removeAccess(@PathVariable Long id, @RequestBody UserType getAccess, @RequestParam long userId) {

        int status = processService.removeAccess(id, userId, getAccess);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Access removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have access."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @PutMapping("/processes/{id}/addEdit")
    public ResponseEntity<ResponseMessage> addEdit(@PathVariable Long id, @RequestBody UserType getEdit, @RequestParam long userId) {

        int status = processService.addEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing granted."));
        } else if(status == 4){
            return ResponseEntity.badRequest().body(new ResponseMessage("User already can edit."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @PutMapping("/processes/{id}/removeEdit")
    public ResponseEntity<ResponseMessage> removeEdit(@PathVariable Long id, @RequestBody UserType getEdit, @RequestParam long userId) {

        int status = processService.removeEdit(id, userId, getEdit);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Editing removed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("User don't have editing rights."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else if(status == 6){
            return ResponseEntity.badRequest().body(new ResponseMessage("At least one editing user must remain."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/processes/{id}/generateHTML")
    public ResponseEntity<StreamingResponseBody> processHTML(@PathVariable Long id) {
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"HTML_report.zip\"")
                .body(out -> processService.generateHTML(id,out));
    }
    @PutMapping("/processes/{id}/createSnapshot")
    public ResponseEntity<ResponseMessage> createSnapshot(@PathVariable Long id, @RequestBody String description, @RequestParam long userId){
        int ret = processService.createSnapshot(id, userId, description);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " created snapshot"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @PutMapping("/processes/restore")
    public ResponseEntity<ResponseMessage> restoreProcess(@RequestBody SnapshotProcess snapshot, @RequestParam long userId){
        Process ret = processService.restoreProcess(userId, snapshot);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Process restored, new id is " + ret.getId()));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process not restored"));
        }
    }

    @PutMapping("/processes/revert")
    public ResponseEntity<ResponseMessage> revertProcess(@RequestBody SnapshotProcess snapshot, @RequestParam long userId){
        Process ret = processService.revertProcess(userId, snapshot);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Process reverted" ));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process not reverted"));
        }
    }

    @PutMapping("/processes/{id}/changeOrder")
    public ResponseEntity<ResponseMessage> removeEdit(@PathVariable Long id, @RequestBody List<Long> order, @RequestParam long userId) {
        int status = processService.changeElementOrder(id, userId, order);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Order changed."));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Bad order."));
        }else if(status == 5){
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process."));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @PutMapping("/processes/{id}/newConfiguration")
    public ResponseEntity<ResponseMessage> newConfig(@PathVariable Long id, @RequestParam long projectId, @RequestParam long userId){
        Process ret = processService.createNewConfiguration(userId, id, projectId);
        if(ret != null){
            return ResponseEntity.ok(new ResponseMessage("Configuration created, new id is " + ret.getId()));
        }else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Configuration not created"));
        }
    }
}
