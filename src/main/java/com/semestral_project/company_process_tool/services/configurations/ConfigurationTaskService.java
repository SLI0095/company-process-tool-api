package com.semestral_project.company_process_tool.services.configurations;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.snapshots.*;
import com.semestral_project.company_process_tool.repositories.RasciRepository;
import com.semestral_project.company_process_tool.repositories.TaskRepository;
import com.semestral_project.company_process_tool.repositories.TaskStepRepository;
import com.semestral_project.company_process_tool.repositories.WorkItemRepository;
import com.semestral_project.company_process_tool.services.BPMNparser;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotsHelper;
import com.semestral_project.company_process_tool.utils.BPMNSnapshotUtil;
import com.semestral_project.company_process_tool.utils.CompanyProcessToolConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationTaskService {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    ConfigurationWorkItemService configurationWorkItemService;
    @Autowired
    TaskStepRepository taskStepRepository;
    @Autowired
    ConfigurationRoleService configurationRoleService;
    @Autowired
    RasciRepository rasciRepository;
    @Autowired
    BPMNparser bpmNparser;
    public Task createNewConfiguration(Task defaultTask, ConfigurationHelper helper, BPMNSnapshotUtil workflow, User user, Project project){
        if(helper == null){
            helper = new ConfigurationHelper();
        }
        boolean updateWorkflow = workflow != null;
        Task task = new Task();
        task.setName(defaultTask.getName());
        task.setBriefDescription(defaultTask.getBriefDescription());
        task.setMainDescription(defaultTask.getMainDescription());
        task.setVersion(defaultTask.getVersion());
        task.setChangeDate(defaultTask.getChangeDate());
        task.setChangeDescription(defaultTask.getChangeDescription());
        task.setPurpose(defaultTask.getPurpose());
        task.setKeyConsiderations(defaultTask.getKeyConsiderations());
        task.setTaskType(defaultTask.getTaskType());
        task.setTemplate(true);

        task.setProject(project);
        task.setOwner(project.getProjectOwner());

        task = taskRepository.save(task);

        //All inputs
        for(WorkItem defaultWorkItem : defaultTask.getMandatoryInputs()){
            //Check if was not snapshot already created during snapshotting
            WorkItem workItem = helper.getExistingWorkItem(defaultWorkItem.getId());
            if(workItem == null){
                workItem = configurationWorkItemService.createNewConfiguration(defaultWorkItem, helper, user, project);
            }
            var asInput = workItem.getAsMandatoryInput();
            asInput.add(task);
            workItem.setAsMandatoryInput(asInput);
            workItemRepository.save(workItem);
            if(updateWorkflow){
                //Change old id in workflow
                String content = workflow.toString();
                String originalId = CompanyProcessToolConst.WORKITEM_ + defaultWorkItem.getId() + "_";
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId() + "_";
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.changeTo(content);
            }
        }

        //All outputs
        for(WorkItem defaultWorkItem : defaultTask.getOutputs()){
            WorkItem workItem = helper.getExistingWorkItem(defaultWorkItem.getId());
            if(workItem == null){
                workItem = configurationWorkItemService.createNewConfiguration(defaultWorkItem, helper, user, project);
            }
            var asOutput = workItem.getAsOutput();
            asOutput.add(task);
            workItem.setAsOutput(asOutput);
            workItemRepository.save(workItem);
            if(updateWorkflow){
                //Change old id in workflow
                String content = workflow.toString();
                String originalId = CompanyProcessToolConst.WORKITEM_ + defaultWorkItem.getId() + "_";
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId() + "_";
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.changeTo(content);
            }
        }

        for(TaskStep defaultStep : defaultTask.getSteps()){
            TaskStep taskStep = new TaskStep();
            taskStep.setName(defaultStep.getName());
            taskStep.setDescription(defaultStep.getDescription());
            taskStep.setTask(task);
            taskStepRepository.save(taskStep);
        }

        for(Rasci defaultRasci : defaultTask.getRasciList()){
            Role defaultRasciRole = defaultRasci.getRole();
            Role role = helper.getExistingRole(defaultRasciRole.getId());
            if(role == null){
                role = configurationRoleService.createNewConfiguration(defaultRasciRole, helper, user, project);
            }
            Rasci rasci = new Rasci();
            rasci.setType(defaultRasci.getType());
            rasci.setRole(role);
            rasci.setTask(task);
            rasciRepository.save(rasci);
        }
        if(updateWorkflow){
            //Change old id in workflow
            String content = workflow.toString();
            String originalId = CompanyProcessToolConst.ELEMENT_ + defaultTask.getId() + "_";
            String newId = CompanyProcessToolConst.ELEMENT_ + task.getId() + "_";
            content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
            workflow.changeTo(content);
        }
        helper.addElement(defaultTask.getId(), task);
        return task;
    }
}
