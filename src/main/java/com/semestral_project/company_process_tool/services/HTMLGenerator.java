package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HTMLGenerator {
    @Autowired
    ProcessService processService;

    private static String head =
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<body>";
    private static String footer =
            "</body>\n" +
            "</html>";

    private List<Role> rolesToGenerate = new ArrayList<>();
    private List<Task> tasksToGenerate = new ArrayList<>();
    private List<WorkItem> workItemsToGenerate = new ArrayList<>();
    /*
    Process name
    Workflow image
    Process detail without name
    process metrics
    Elements in process - name with reference (subprocesses only info)
    Rasci matrix
    Detail of all tasks
        info
        steps
        work items
            inputs - reference
            outputs - reference
            guidance - reference
     Detail of all roles
        info
     Detail of all work items
        info
        states
        relations
     */

    public String generateHTML(long processId){
        this.tasksToGenerate = new ArrayList<>();
        this.rolesToGenerate = new ArrayList<>();
        this.workItemsToGenerate = new ArrayList<>();
        StringBuilder htmlBuilder = new StringBuilder();

        Process process = processService.getProcessById(processId);
        htmlBuilder.append(head);
        htmlBuilder.append("<h1>" + process.getName() + "</h1>");
        //workflow image
        htmlBuilder.append(processDetail(process));
        htmlBuilder.append(processMetrics(process));
        htmlBuilder.append(processElements(process));
        //matrix
        htmlBuilder.append("<h1>Tasks</h1>");
        for(Task t : tasksToGenerate){
            htmlBuilder.append(taskPart(t));
        }
        htmlBuilder.append("<h1>Roles</h1>");
        for(Role r : rolesToGenerate){
            htmlBuilder.append(rolePart(r));
        }
        htmlBuilder.append("<h1>Work items</h1>");
        for(WorkItem w : workItemsToGenerate){
            htmlBuilder.append(workItemPart(w));
        }
        htmlBuilder.append(footer);
        return htmlBuilder.toString();
    }

    private String processDetail(Process process){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append(generatePart("Brief description:", process.getBriefDescription()));
        returnString.append(generatePart("Main description:", process.getMainDescription()));
        returnString.append(generatePart("Purpose:", process.getPurpose()));
        returnString.append(generatePart("Scope:", process.getScope()));
        returnString.append(generatePart("Usage notes:", process.getUsageNotes()));
        returnString.append(generatePart("Alternatives:", process.getAlternatives()));
        returnString.append(generatePart("How to staff:", process.getHowToStaff()));
        returnString.append(generatePart("Key considerations:", process.getKeyConsiderations()));
        returnString.append(generatePart("Version:", process.getVersion()));
        returnString.append(generatePart("Change date:", process.getChangeDate().toString()));
        returnString.append(generatePart("Change description:", process.getChangeDescription()));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String processMetrics(Process process){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<h3>Process metrics</h3>");
        returnString.append("<dl>");
        for(ProcessMetric metric : process.getMetrics()){
            returnString.append("<dt>" + metric.getName() + "</dt>");
            returnString.append("<dd>" + metric.getDescription() + "</dd>");
        }
        returnString.append("</dl>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String processElements(Process process){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<h3>Activities in process</h3>");
        returnString.append("<dl>");
        for(Element e : process.getElements()){
            if(e.getClass() == Task.class)
            {
                returnString.append("<dt><a href='element_" + e.getId() + "'>" + e.getName() + "</a></dt>");
                this.tasksToGenerate.add((Task) e);
            } else {
                returnString.append("<dt>" + e.getName() + "</dt>");
            }
            returnString.append("<dd>" + e.getBriefDescription() + "</dd>");
        }
        returnString.append("</dl>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskPart(Task task){
        StringBuilder returnString = new StringBuilder();
        for(Rasci rasci : task.getRasciList())
        {
            Role role = rasci.getRole();
            if(!rolesToGenerate.contains(role)){
                rolesToGenerate.add(role);
            }
        }
        returnString.append("<div id='element_"+task.getId() +"' >");
        returnString.append("<h2>" + task.getName() + "</h2>");
        returnString.append(taskDetail(task));
        returnString.append(taskSteps(task));
        returnString.append(taskInputs(task));
        returnString.append(taskOutputs(task));
        returnString.append(taskGuidance(task));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskDetail(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append(generatePart("Brief description:", task.getBriefDescription()));
        returnString.append(generatePart("Main description:", task.getMainDescription()));
        returnString.append(generatePart("Purpose:", task.getPurpose()));
        returnString.append(generatePart("Key considerations:", task.getKeyConsiderations()));
        returnString.append(generatePart("Version:", task.getVersion()));
        returnString.append(generatePart("Change date:", task.getChangeDate().toString()));
        returnString.append(generatePart("Change description:", task.getChangeDescription()));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskSteps(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<h3>Task steps</h3>");
        returnString.append("<dl>");
        for(TaskStep step : task.getSteps()){
            returnString.append("<dt>" + step.getName() + "</dt>");
            returnString.append("<dd>" + step.getDescription() + "</dd>");
        }
        returnString.append("</dl>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskInputs(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<h3>Task inputs</h3>");
        returnString.append("<dl>");
        for(WorkItem workItem : task.getMandatoryInputs()){
            if(!workItemsToGenerate.contains(workItem)){
                workItemsToGenerate.add(workItem);
            }
            returnString.append("<dt><a href='workItem_" + workItem.getId() + "'>" + workItem.getName() + "</a></dt>");
            returnString.append("<dd>" + workItem.getBriefDescription() + "</dd>");
        }
        returnString.append("</dl>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskOutputs(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<h3>Task outputs</h3>");
        returnString.append("<dl>");
        for(WorkItem workItem : task.getOutputs()){
            if(!workItemsToGenerate.contains(workItem)){
                workItemsToGenerate.add(workItem);
            }
            returnString.append("<dt><a href='workItem_" + workItem.getId() + "'>" + workItem.getName() + "</a></dt>");
            returnString.append("<dd>" + workItem.getBriefDescription() + "</dd>");
        }
        returnString.append("</dl>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskGuidance(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<h3>Task guidance work items</h3>");
        returnString.append("<dl>");
        for(WorkItem workItem : task.getGuidanceWorkItems()){
            if(!workItemsToGenerate.contains(workItem)){
                workItemsToGenerate.add(workItem);
            }
            returnString.append("<dt><a href='workItem_" + workItem.getId() + "'>" + workItem.getName() + "</a></dt>");
            returnString.append("<dd>" + workItem.getBriefDescription() + "</dd>");
        }
        returnString.append("</dl>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String rolePart(Role role){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div id='role_"+role.getId() +"' >");
        returnString.append("<h2>" + role.getName() + "</h2>");
        returnString.append(roleDetail(role));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String roleDetail(Role role){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append(generatePart("Brief description:", role.getBriefDescription()));
        returnString.append(generatePart("Main description:", role.getMainDescription()));
        returnString.append(generatePart("Skills:", role.getSkills()));
        returnString.append(generatePart("Assignment approaches:", role.getAssignmentApproaches()));
        returnString.append(generatePart("Version:", role.getVersion()));
        returnString.append(generatePart("Change date:", role.getChangeDate().toString()));
        returnString.append(generatePart("Change description:", role.getChangeDescription()));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String workItemPart(WorkItem workItem){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div id='workItem_"+workItem.getId() +"' >");
        returnString.append("<h2>" + workItem.getName() + "</h2>");
        returnString.append(workItemDetail(workItem));
        returnString.append(workItemStates(workItem));
        returnString.append(workItemRelations(workItem));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String workItemDetail(WorkItem workItem){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append(generatePart("Brief description:", workItem.getBriefDescription()));
        returnString.append(generatePart("Main description:", workItem.getMainDescription()));
        returnString.append(generatePart("Work item type:", workItem.getWorkItemType()));
        returnString.append(generatePart("Source:", workItem.getUrlAddress()));
        returnString.append(generatePart("Purpose:", workItem.getPurpose()));
        returnString.append(generatePart("Key considerations:", workItem.getKeyConsiderations()));
        returnString.append(generatePart("Brief outline:", workItem.getBriefOutline()));
        returnString.append(generatePart("Notation:", workItem.getNotation()));
        returnString.append(generatePart("Impact of not having:", workItem.getImpactOfNotHaving()));
        returnString.append(generatePart("Reasons for not needing:", workItem.getReasonForNotNeeding()));
        returnString.append(generatePart("Templates:", workItem.getTemplateText()));
        returnString.append(generatePart("Version:", workItem.getVersion()));
        returnString.append(generatePart("Change date:", workItem.getChangeDate().toString()));
        returnString.append(generatePart("Change description:", workItem.getChangeDescription()));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String workItemStates(WorkItem workItem){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<h3>Possible states</h3>");
        returnString.append("<dl>");
        for(State state : workItem.getWorkItemStates()){
            returnString.append("<dt>" + state.getStateName() + "</dt>");
            returnString.append("<dd>" + state.getStateDescription() + "</dd>");
        }
        returnString.append("</dl>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String workItemRelations(WorkItem workItem){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<h3>Relations to other work items</h3>");
        returnString.append("<dl>");
        for(WorkItemRelation relation : workItem.getRelationsToAnotherWorkItems()){
            WorkItem wi = relation.getRelatedWorkItem();
            if(workItemsToGenerate.contains(relation.getRelatedWorkItem())){
                returnString.append("<dt><a href='workItem_" + wi.getId() + "'>" + wi.getName() + "</a></dt>");
            } else {
                returnString.append("<dt>" + wi.getName() + "</dt>");
            }
            returnString.append("<dd>Relation type: " + relation.getRelationType() + "</dd>");
            returnString.append("<dd>" + wi.getBriefDescription() + "</dd>");
        }
        returnString.append("</dl>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String generatePart(String name, String content){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<label>" + name + "</label>");
        returnString.append("<p>" + content + "</p>");
        returnString.append("</div>");

        return returnString.toString();
    }
}
