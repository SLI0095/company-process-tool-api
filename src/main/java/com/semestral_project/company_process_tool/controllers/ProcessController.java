package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.ElementRepository;
import com.semestral_project.company_process_tool.repositories.ProcessRepository;
import com.semestral_project.company_process_tool.services.ProcessService;
import com.semestral_project.company_process_tool.services.RasciMatrixService;
import com.semestral_project.company_process_tool.utils.ProcessAndBpmnHolder;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProcessController {

    @Autowired
    ProcessService processService;
    @Autowired
    RasciMatrixService rasciMatrixService;

    @GetMapping("/processes")
    public ResponseEntity<List<Process>> getProcesses() {
        List<Process> processes = processService.getAllProcesses();
        if(processes != null){
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/processes/templates")
    public ResponseEntity<List<Process>> getProcessesTemplates(@RequestParam long userId) {
        List<Process> processes = processService.getAllTemplates(userId);
        if(processes != null){
            return ResponseEntity.ok(processes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/processes/{id}")
    public ResponseEntity<Process> processById(@PathVariable Long id) {
        Process process = processService.getProcessById(id);
        if(process != null){
            return ResponseEntity.ok(process);
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

    @PostMapping("/processes")
    public ResponseEntity<ResponseMessage> addProcess(@RequestBody Process process, @RequestParam long userId){
        long ret = processService.addProcess(process, userId);
        if(ret != -1){
            return ResponseEntity.ok(new ResponseMessage("Process added"));
        } else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit project."));
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

//    @PutMapping("/processes/{id}/addElement")
//    public ResponseEntity<ResponseMessage> addElement(@PathVariable Long id, @RequestBody Element element){
//        int ret = processService.addElementToProcess(id, element);
//        if(ret == 1){
//            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
//        } else if(ret == 2){
//            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
//        } else {
//            return ResponseEntity.badRequest().body(new ResponseMessage("Element already added"));
//        }
//    }
//
//
//    @PutMapping("/processes/{id}/removeElement")
//    public ResponseEntity<ResponseMessage> removeElement(@PathVariable Long id, @RequestBody Element element){
//        int ret = processService.removeElementFromProcess(id, element);
//        if(ret == 1){
//            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
//        } else if(ret == 2){
//            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
//        } else {
//            return ResponseEntity.badRequest().body(new ResponseMessage("Element not in activity id: " + id));
//        }
//    }

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

    @PutMapping("/processes/{id}/restoreBPMN")
    public ResponseEntity<ResponseMessage> restoreBPMN(@PathVariable Long id, @RequestBody HistoryBPMN bpmn, @RequestParam long userId){

        int ret = processService.restoreWorkflow(id, bpmn, userId);
        if(ret == 1){
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated. Revert successful."));
        }else if(ret == 3) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User cannot edit this process"));
        } else if(ret == 2) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
        else {
            return ResponseEntity.badRequest().body(new ResponseMessage("This version of BPMN can not be reverted"));
        }
    }

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
}
