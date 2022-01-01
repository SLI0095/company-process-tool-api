package com.semestral_project.company_process_tool.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.semestral_project.company_process_tool.entities.ActivityOld;
import com.semestral_project.company_process_tool.entities.DocumentOld;
import com.semestral_project.company_process_tool.entities.InputOutput;
import com.semestral_project.company_process_tool.repositories.ActivityRepository;
import com.semestral_project.company_process_tool.repositories.DocumentRepository;
import com.semestral_project.company_process_tool.repositories.InputOutputRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import com.semestral_project.company_process_tool.utils.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ActivityController {

    private final ActivityRepository activityRepository;
    private final DocumentRepository documentRepository;
    private final InputOutputRepository inputOutputRepository;

    public ActivityController(ActivityRepository activityRepository, DocumentRepository documentRepository, InputOutputRepository inputOutputRepository) {
        this.activityRepository = activityRepository;
        this.documentRepository = documentRepository;
        this.inputOutputRepository = inputOutputRepository;
    }

    @JsonView(Views.Minimal.class)
    @GetMapping("/activities")
    public  ResponseEntity<List<ActivityOld>> getActivities(){
        try {
            return ResponseEntity.ok((List<ActivityOld>) activityRepository.findAll());
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }

    }

    @PostMapping("/activities")
    public ResponseEntity<ResponseMessage> addActivity(@RequestBody ActivityOld activity){
        try {
            activityRepository.save(activity);
            return ResponseEntity.ok(new ResponseMessage("Activity added"));
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/activities/{id}")
    public ResponseEntity<ResponseMessage> removeActivity(@PathVariable Long id) {
        try
        {
            activityRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Activity id: " + id + " is deleted"));
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @JsonView(Views.ActivityGeneral.class)
    @GetMapping("/activities/{id}")
    public ResponseEntity<ActivityOld> activityById(@PathVariable Long id) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);
        if(activityData.isPresent()){
            return ResponseEntity.ok(activityData.get());
        }
        else return ResponseEntity.badRequest().header("Activity id: " + id + " does not exist").body(null);
    }

    @JsonView(Views.ActivityDocuments.class)
    @GetMapping("/activities/{id}/documents")
    public ResponseEntity<ActivityOld> activityDocuments(@PathVariable Long id) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);

        if(activityData.isPresent()){
            return ResponseEntity.ok(activityData.get());
        }
        else return ResponseEntity.badRequest().header("Activity id: " + id + " does not exist").body(null);
    }

    @JsonView(Views.ActivityInputs.class)
    @GetMapping("/activities/{id}/inputs")
    public ResponseEntity<ActivityOld> activityInputs(@PathVariable Long id) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);

        if(activityData.isPresent()){
            return ResponseEntity.ok(activityData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @JsonView(Views.ActivityOutputs.class)
    @GetMapping("/activities/{id}/outputs")
    public ResponseEntity<ActivityOld> activityOutputs(@PathVariable Long id) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);

        if(activityData.isPresent()){
            return ResponseEntity.ok(activityData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @JsonView(Views.ActivityRasci.class)
    @GetMapping("/activities/{id}/rasci")
    public ResponseEntity<ActivityOld> activityRasci(@PathVariable Long id) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);

        if(activityData.isPresent()){
            return ResponseEntity.ok(activityData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @JsonView(Views.Minimal.class)
    @GetMapping("/activities/{id}/nextActivities")
    public ResponseEntity<List<ActivityOld>> activityNextActivities(@PathVariable Long id) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);

        if(activityData.isPresent()){
            ActivityOld activity_ = activityData.get();
            return ResponseEntity.ok(activity_.getNextActivities());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PutMapping("/activities/{id}")
    public ResponseEntity<ResponseMessage> updateActivity(@PathVariable Long id, @RequestBody ActivityOld activity) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);

        if(activityData.isPresent()){
            ActivityOld activity_ = activityData.get();

            activity_.setName(activity.getName());
            activity_.setDescription(activity.getDescription());
            if(activity.getProcess() != null)
            {
                activity_.setProcess(activity.getProcess());
            }
            activity_.setPreviousActivity(activity.getPreviousActivity());
            activityRepository.save(activity_);
            return ResponseEntity.ok(new ResponseMessage("Activity id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }

    @PutMapping("/activities/{id}/addDocument")
    public ResponseEntity<ResponseMessage> addDocument(@PathVariable Long id, @RequestBody DocumentOld document) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);
        if(activityData.isPresent()){
            ActivityOld activity_ = activityData.get();
            List<DocumentOld> documents = activity_.getDocuments();
            for(DocumentOld doc : documents)
            {
                if(doc.getId() == document.getId())
                {
                    return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + document.getId() +  " already added."));
                }
            }
            Optional<DocumentOld> documentData = documentRepository.findById(document.getId());
            if(documentData.isPresent())
            {
                DocumentOld document_ = documentData.get();
                activity_.addDocument(document_);
                activityRepository.save(activity_);
                documentRepository.save(document_);
                return ResponseEntity.ok(new ResponseMessage("Document id: " + document.getId() +  " added"));
            }
            else
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + document.getId() + " does not exist."));
            }
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/activities/{id}/removeDocument")
    public ResponseEntity<ResponseMessage> removeDocument(@PathVariable Long id, @RequestBody DocumentOld document) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);
        if (activityData.isPresent()) {
            ActivityOld activity_ = activityData.get();
            List<DocumentOld> documents = activity_.getDocuments();
            for (DocumentOld doc : documents) {
                if (doc.getId() == document.getId()) {
                    Optional<DocumentOld> documentData = documentRepository.findById(document.getId());
                    if (documentData.isPresent()) {
                        DocumentOld document_ = documentData.get();
                        activity_.removeDocument(document_);
                        activityRepository.save(activity_);
                        documentRepository.save(document_);
                        return ResponseEntity.ok(new ResponseMessage("Document id: " + document.getId() + " was removed"));
                    } else {
                        return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + document.getId() + " does not exist."));
                    }
                }
            }
            return ResponseEntity.badRequest().body(new ResponseMessage("Document id: " + document.getId() + " not in documents of Activity: " + id + "."));
        }
        return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
    }

    @PutMapping("/activities/{id}/addInput")
    public ResponseEntity<ResponseMessage> addInput(@PathVariable Long id, @RequestBody InputOutput input) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);
        if(activityData.isPresent()){
            ActivityOld activity_ = activityData.get();
            List<InputOutput> inputs = activity_.getInputs();
            for(InputOutput inp : inputs)
            {
                if(inp.getId() == input.getId())
                {
                    return ResponseEntity.badRequest().body(new ResponseMessage("Input id: " + input.getId() +  " already added."));
                }
            }
            Optional<InputOutput> inputData = inputOutputRepository.findById(input.getId());
            if(inputData.isPresent())
            {
                InputOutput input_ = inputData.get();
                activity_.addInput(input_);
                activityRepository.save(activity_);
                inputOutputRepository.save(input_);
                return ResponseEntity.ok(new ResponseMessage("Input id: " + input.getId() +  " added"));
            }
            else
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Input id: " + input.getId() + " does not exist."));
            }
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/activities/{id}/removeInput")
    public ResponseEntity<ResponseMessage> removeInput(@PathVariable Long id, @RequestBody InputOutput input) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);
        if (activityData.isPresent()) {
            ActivityOld activity_ = activityData.get();
            List<InputOutput> inputs = activity_.getInputs();
            for (InputOutput inp : inputs) {
                if (inp.getId() == input.getId()) {
                    Optional<InputOutput> inputData = inputOutputRepository.findById(input.getId());
                    if (inputData.isPresent()) {
                        InputOutput input_ = inputData.get();
                        activity_.removeInput(input_);
                        activityRepository.save(activity_);
                        inputOutputRepository.save(input_);
                        return ResponseEntity.ok(new ResponseMessage("Input id: " + input.getId() + " was removed"));
                    } else {
                        return ResponseEntity.badRequest().body(new ResponseMessage("Input id: " + input.getId() + " does not exist."));
                    }
                }
            }
            return ResponseEntity.badRequest().body(new ResponseMessage("Input id: " + input.getId() + " not in inputs of Activity: " + id + "."));
        }
        return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
    }

    @PutMapping("/activities/{id}/addOutput")
    public ResponseEntity<ResponseMessage> addOutput(@PathVariable Long id, @RequestBody InputOutput output) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);
        if(activityData.isPresent()){
            ActivityOld activity_ = activityData.get();
            List<InputOutput> outputs = activity_.getOutputs();
            for(InputOutput out : outputs)
            {
                if(out.getId() == output.getId())
                {
                    return ResponseEntity.badRequest().body(new ResponseMessage("Output id: " + output.getId() +  " already added."));
                }
            }
            Optional<InputOutput> outputData = inputOutputRepository.findById(output.getId());
            if(outputData.isPresent())
            {
                InputOutput output_ = outputData.get();
                activity_.addOutput(output_);
                activityRepository.save(activity_);
                inputOutputRepository.save(output_);
                return ResponseEntity.ok(new ResponseMessage("Output id: " + output.getId() +  " added"));
            }
            else
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Output id: " + output.getId() + " does not exist."));
            }
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }

    @DeleteMapping("/activities/{id}/removeOutput")
    public ResponseEntity<ResponseMessage> removeOutput(@PathVariable Long id, @RequestBody InputOutput output) {
        Optional<ActivityOld> activityData = activityRepository.findById(id);
        if (activityData.isPresent()) {
            ActivityOld activity_ = activityData.get();
            List<InputOutput> outputs = activity_.getOutputs();
            for (InputOutput out : outputs) {
                if (out.getId() == output.getId()) {
                    Optional<InputOutput> inputData = inputOutputRepository.findById(output.getId());
                    if (inputData.isPresent()) {
                        InputOutput output_ = inputData.get();
                        activity_.removeOutput(output_);
                        activityRepository.save(activity_);
                        inputOutputRepository.save(output_);
                        return ResponseEntity.ok(new ResponseMessage("Output id: " + output.getId() + " was removed"));
                    } else {
                        return ResponseEntity.badRequest().body(new ResponseMessage("Output id: " + output.getId() + " does not exist."));
                    }
                }
            }
            return ResponseEntity.badRequest().body(new ResponseMessage("Output id: " + output.getId() + " not in inputs of Activity: " + id + "."));
        }
        return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
    }
}
