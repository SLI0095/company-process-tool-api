package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.ElementRepository;
import com.semestral_project.company_process_tool.repositories.ProcessRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProcessController {

    @Autowired
    ProcessRepository processRepository;
    @Autowired
    ElementRepository elementRepository;

    @GetMapping("/processes")
    public ResponseEntity<List<Process>> getProcesses() {
        try {
            return ResponseEntity.ok((List<Process>) processRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }

    @GetMapping("/processes/{id}")
    public ResponseEntity<Process> processById(@PathVariable Long id) {
        Optional<Process> processData = processRepository.findById(id);

        if(processData.isPresent()) {
            return ResponseEntity.ok(processData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/processes")
    public ResponseEntity<ResponseMessage> addProcess(@RequestBody Process process){
        try {
            processRepository.save(process);
            return ResponseEntity.ok(new ResponseMessage("Process added"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/processes/{id}")
    public ResponseEntity<ResponseMessage> removeProcess(@PathVariable Long id) {
        try {
            processRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is deleted"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/processes/{id}")
    public ResponseEntity<ResponseMessage> updateProcess(@PathVariable Long id, @RequestBody Process process) {
        Optional<Process> processData = processRepository.findById(id);

        if(processData.isPresent()){
            Process process_ = processData.get();
            process_ = fillProcess(process_, process);

            processRepository.save(process_);
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    @PutMapping("/processes/{id}/addElement")
    public ResponseEntity<ResponseMessage> addElement(@PathVariable Long id, @RequestBody Element element){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            Element element_ = elementRepository.findById(element.getId()).get();
            var elementList = process_.getElements();
            if(elementList.contains(element_))
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Element already added"));
            }
            elementList.add(element_);
            process_.setElements(elementList);

            processRepository.save(process_);
            return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated. Element added."));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }


    @PutMapping("/processes/{id}/removeElement")
    public ResponseEntity<ResponseMessage> removeElement(@PathVariable Long id, @RequestBody Element element){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            Element element_ = elementRepository.findById(element.getId()).get();
            var elementList = process_.getElements();
            if(elementList.contains(element_)) {
                elementList.remove(element_);
                process_.setElements(elementList);
                processRepository.save(process_);
                return ResponseEntity.ok(new ResponseMessage("Process id: " + id + " is updated. Element removed."));

            }
            else {
                return ResponseEntity.badRequest().body(new ResponseMessage("Element not in activity id: " + id));
            }

        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Process id: " + id + " does not exist"));
        }
    }

    private Process fillProcess(Process oldProcess, Process updatedProcess){
        oldProcess.setName(updatedProcess.getName());
        oldProcess.setBriefDescription(updatedProcess.getBriefDescription());
        oldProcess.setMainDescription(updatedProcess.getMainDescription());
        oldProcess.setVersion(updatedProcess.getVersion());
        oldProcess.setChangeDate(updatedProcess.getChangeDate());
        oldProcess.setChangeDescription(updatedProcess.getChangeDescription());
        oldProcess.setPurpose(updatedProcess.getPurpose());
        oldProcess.setScope(updatedProcess.getScope());
        oldProcess.setUsageNotes(updatedProcess.getUsageNotes());
        oldProcess.setHowToStaff(updatedProcess.getHowToStaff());
        oldProcess.setKeyConsiderations(updatedProcess.getKeyConsiderations());
        return oldProcess;
    }
}
