package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.repositories.ActivityRepository;
import com.semestral_project.company_process_tool.repositories.ElementRepository;
import com.semestral_project.company_process_tool.repositories.WorkItemRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ActivityController {
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    ElementRepository elementRepository;

    @GetMapping("/activities")
    public ResponseEntity<List<Activity>> getActivities() {
        try {
            return ResponseEntity.ok((List<Activity>) activityRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }

    @GetMapping("/activities/{id}")
    public ResponseEntity<Activity> activityById(@PathVariable Long id) {
        Optional<Activity> activityData = activityRepository.findById(id);

        if(activityData.isPresent()) {
            return ResponseEntity.ok(activityData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/activities")
    public ResponseEntity<ResponseMessage> addActivity(@RequestBody Activity activity){
        try {
            activityRepository.save(activity);
            return ResponseEntity.ok(new ResponseMessage("Activity added"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/activities/{id}")
    public ResponseEntity<ResponseMessage> removeActivity(@PathVariable Long id) {
        try {
            activityRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Activity id: " + id + " is deleted"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/activities/{id}")
    public ResponseEntity<ResponseMessage> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        Optional<Activity> activityData = activityRepository.findById(id);

        if(activityData.isPresent()){
            Activity activity_ = activityData.get();
            activity_ = fillActivity(activity_, activity);

            activityRepository.save(activity_);
            return ResponseEntity.ok(new ResponseMessage("Activity id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }


    @PutMapping("/activities/{id}/addGuidance")
    public ResponseEntity<ResponseMessage> addGuidance(@PathVariable Long id, @RequestBody WorkItem workItem){
        Optional<Activity> activityData = activityRepository.findById(id);
        if(activityData.isPresent()) {
            Activity activity_ = activityData.get();
            WorkItem item_ = workItemRepository.findById(workItem.getId()).get();
            var guidanceList = activity_.getGuidanceWorkItems();
            if(guidanceList.contains(item_))
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Guidance work item already added"));
            }
            guidanceList.add(item_);
            activity_.setGuidanceWorkItems(guidanceList);

            activityRepository.save(activity_);
            return ResponseEntity.ok(new ResponseMessage("Activity id: " + id + " is updated. Guidance work item added."));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }


    @PutMapping("/activities/{id}/removeGuidance")
    public ResponseEntity<ResponseMessage> removeGuidance(@PathVariable Long id, @RequestBody WorkItem item){
        Optional<Activity> activityData = activityRepository.findById(id);
        if(activityData.isPresent()) {
            Activity activity_ = activityData.get();
            WorkItem item_ = workItemRepository.findById(item.getId()).get();
            var guidanceList = activity_.getGuidanceWorkItems();
            if(guidanceList.contains(item_)) {
                guidanceList.remove(item_);
                activity_.setGuidanceWorkItems(guidanceList);
                activityRepository.save(activity_);
                return ResponseEntity.ok(new ResponseMessage("Activity id: " + id + " is updated. Guidance work item removed."));

            }
            else {
                return ResponseEntity.badRequest().body(new ResponseMessage("Guidance work item not in activity id: " + id));
            }

        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }


    @PutMapping("/activities/{id}/addElement")
    public ResponseEntity<ResponseMessage> addElement(@PathVariable Long id, @RequestBody Element element){
        Optional<Activity> activityData = activityRepository.findById(id);
        if(activityData.isPresent()) {
            Activity activity_ = activityData.get();
            Element element_ = elementRepository.findById(element.getId()).get();
            var elementList = activity_.getElements();
            if(elementList.contains(element_))
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Element already added"));
            }
            elementList.add(element_);
            activity_.setElements(elementList);

            activityRepository.save(activity_);
            return ResponseEntity.ok(new ResponseMessage("Activity id: " + id + " is updated. Element added."));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }


    @PutMapping("/activities/{id}/removeElement")
    public ResponseEntity<ResponseMessage> removeElement(@PathVariable Long id, @RequestBody Element element){
        Optional<Activity> activityData = activityRepository.findById(id);
        if(activityData.isPresent()) {
            Activity activity_ = activityData.get();
            Element element_ = elementRepository.findById(element.getId()).get();
            var elementList = activity_.getElements();
            if(elementList.contains(element_)) {
                elementList.remove(element_);
                activity_.setElements(elementList);
                activityRepository.save(activity_);
                return ResponseEntity.ok(new ResponseMessage("Activity id: " + id + " is updated. Element removed."));

            }
            else {
                return ResponseEntity.badRequest().body(new ResponseMessage("Element not in activity id: " + id));
            }

        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Activity id: " + id + " does not exist"));
        }
    }

    private Activity fillActivity(Activity oldActivity, Activity updatedActivity){
        oldActivity.setName(updatedActivity.getName());
        oldActivity.setBriefDescription(updatedActivity.getBriefDescription());
        oldActivity.setMainDescription(updatedActivity.getMainDescription());
        oldActivity.setVersion(updatedActivity.getVersion());
        oldActivity.setChangeDate(updatedActivity.getChangeDate());
        oldActivity.setChangeDescription(updatedActivity.getChangeDescription());
        return oldActivity;
    }
}
