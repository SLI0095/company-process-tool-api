package com.semestral_project.company_process_tool.services.snaphsots;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.snapshots.*;
import com.semestral_project.company_process_tool.repositories.RasciRepository;
import com.semestral_project.company_process_tool.repositories.TaskRepository;
import com.semestral_project.company_process_tool.repositories.TaskStepRepository;
import com.semestral_project.company_process_tool.repositories.WorkItemRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotRasciRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotTaskRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotTaskStepRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotWorkItemRepository;
import com.semestral_project.company_process_tool.services.BPMNparser;
import com.semestral_project.company_process_tool.utils.CompanyProcessToolConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SnapshotTaskService {

    @Autowired
    SnapshotTaskRepository snapshotTaskRepository;
    @Autowired
    SnapshotTaskStepRepository snapshotTaskStepRepository;
    @Autowired
    SnapshotRasciRepository snapshotRasciRepository;
    @Autowired
    SnapshotWorkItemService snapshotWorkItemService;
    @Autowired
    SnapshotRoleService snapshotRoleService;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskStepRepository taskStepRepository;
    @Autowired
    RasciRepository rasciRepository;
    @Autowired
    BPMNparser bpmNparser;
    @Autowired
    SnapshotWorkItemRepository snapshotWorkItemRepository;
    @Autowired
    WorkItemRepository workItemRepository;

    public SnapshotTask createSnapshot(Task original, String snapshotDescription, SnapshotsHelper helper){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        SnapshotTask snapshot = new SnapshotTask();
        snapshot.setName(original.getName());
        snapshot.setBriefDescription(original.getBriefDescription());
        snapshot.setMainDescription(original.getMainDescription());
        snapshot.setVersion(original.getVersion());
        snapshot.setChangeDate(original.getChangeDate());
        snapshot.setChangeDescription(original.getChangeDescription());
        snapshot.setPurpose(original.getPurpose());
        snapshot.setKeyConsiderations(original.getKeyConsiderations());
        snapshot.setTaskType(original.getTaskType());

        snapshot.setOriginalElement(original);
        snapshot.setSnapshotDescription(snapshotDescription);
        snapshot.setSnapshotDate(LocalDate.now());
        snapshot.setOriginalId(original.getId());

        snapshot = snapshotTaskRepository.save(snapshot);

        //All inputs
        for(WorkItem workItem : original.getMandatoryInputs()){
            //Check if was not snapshot already created during snapshotting
            SnapshotWorkItem snapshotWorkItem = helper.getExistingSnapshotWorkItem(workItem.getId());
            if(snapshotWorkItem == null){
                snapshotWorkItem = snapshotWorkItemService.createSnapshot(workItem,snapshotDescription, helper);
            }
            var list = snapshotWorkItem.getAsMandatoryInput();
            list.add(snapshot);
            snapshotWorkItem.setAsMandatoryInput(list);
            snapshotWorkItemRepository.save(snapshotWorkItem);
        }

        //All outputs
        for(WorkItem workItem : original.getOutputs()){
            SnapshotWorkItem snapshotWorkItem = helper.getExistingSnapshotWorkItem(workItem.getId());
            if(snapshotWorkItem == null){
                snapshotWorkItem = snapshotWorkItemService.createSnapshot(workItem,snapshotDescription, helper);
            }
            var list = snapshotWorkItem.getAsOutput();
            list.add(snapshot);
            snapshotWorkItem.setAsOutput(list);
            snapshotWorkItemRepository.save(snapshotWorkItem);
        }

        for(TaskStep step : original.getSteps()){
            SnapshotTaskStep snapshotTaskStep = new SnapshotTaskStep();
            snapshotTaskStep.setName(step.getName());
            snapshotTaskStep.setDescription(step.getDescription());
            snapshotTaskStep.setTask(snapshot);
            snapshotTaskStepRepository.save(snapshotTaskStep);
        }

        for(Rasci rasci : original.getRasciList()){
            Role role = rasci.getRole();
            SnapshotRole snapshotRole = helper.getExistingSnapshotRole(role.getId());
            if(snapshotRole == null){
                snapshotRole = snapshotRoleService.createSnapshotRole(role, snapshotDescription, helper);
            }
            SnapshotRasci snapshotRasci = new SnapshotRasci();
            snapshotRasci.setType(rasci.getType());
            snapshotRasci.setRole(snapshotRole);
            snapshotRasci.setTask(snapshot);
            snapshotRasciRepository.save(snapshotRasci);
        }
        helper.addElement(original.getId(), snapshot);

        return snapshot;
    }

    public Task restoreFromSnapshot(SnapshotTask snapshotTask, SnapshotsHelper helper, SnapshotBPMN workflow, User user){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        boolean updateWorkflow = workflow != null;
        Task task = new Task();
        task.setName(snapshotTask.getName());
        task.setBriefDescription(snapshotTask.getBriefDescription());
        task.setMainDescription(snapshotTask.getMainDescription());
        task.setVersion(snapshotTask.getVersion());
        task.setChangeDate(snapshotTask.getChangeDate());
        task.setChangeDescription(snapshotTask.getChangeDescription());
        task.setPurpose(snapshotTask.getPurpose());
        task.setKeyConsiderations(snapshotTask.getKeyConsiderations());
        task.setTaskType(snapshotTask.getTaskType());

        var list = task.getCanEdit();
        list.add(user);
        task.setCanEdit(list);
        task.setOwner(user);

        task = taskRepository.save(task);

        //All inputs
        for(SnapshotWorkItem snapshotWorkItem : snapshotTask.getMandatoryInputs()){
            //Check if was not snapshot already created during snapshotting
            WorkItem workItem = helper.getExistingWorkItem(snapshotWorkItem.getId());
            if(workItem == null){
                workItem = snapshotWorkItemService.restoreFromSnapshot(snapshotWorkItem, helper, user);
            }
            var asInput = workItem.getAsMandatoryInput();
            asInput.add(task);
            workItem.setAsMandatoryInput(asInput);
            workItemRepository.save(workItem);
            if(updateWorkflow){
                //Change old id in workflow
                String content = workflow.getBpmnContent();
                String originalId = CompanyProcessToolConst.WORKITEM_ + snapshotWorkItem.getOriginalId().toString();
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId();
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.setBpmnContent(content);
            }
        }

        //All outputs
        for(SnapshotWorkItem snapshotWorkItem : snapshotTask.getOutputs()){
            WorkItem workItem = helper.getExistingWorkItem(snapshotWorkItem.getId());
            if(workItem == null){
                workItem = snapshotWorkItemService.restoreFromSnapshot(snapshotWorkItem, helper, user);
            }
            var asOutput = workItem.getAsOutput();
            asOutput.add(task);
            workItem.setAsOutput(asOutput);
            workItemRepository.save(workItem);
            if(updateWorkflow){
                //Change old id in workflow
                String content = workflow.getBpmnContent();
                String originalId = CompanyProcessToolConst.WORKITEM_ + snapshotWorkItem.getOriginalId().toString();
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId();
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.setBpmnContent(content);
            }
        }

        for(SnapshotTaskStep snapshotStep : snapshotTask.getSteps()){
            TaskStep taskStep = new TaskStep();
            taskStep.setName(snapshotStep.getName());
            taskStep.setDescription(snapshotStep.getDescription());
            taskStep.setTask(task);
            taskStepRepository.save(taskStep);
        }

        for(SnapshotRasci snapshotRasci : snapshotTask.getRasciList()){
            SnapshotRole snapshotRole = snapshotRasci.getRole();
            Role role = helper.getExistingRole(snapshotRole.getId());
            if(role == null){
                role = snapshotRoleService.restoreRoleFromSnapshot(snapshotRole, helper, user);
            }
            Rasci rasci = new Rasci();
            rasci.setType(snapshotRasci.getType());
            rasci.setRole(role);
            rasci.setTask(task);
            rasciRepository.save(rasci);
        }
        if(updateWorkflow){
            //Change old id in workflow
            String content = workflow.getBpmnContent();
            String originalId = CompanyProcessToolConst.ELEMENT_ + snapshotTask.getOriginalId().toString();
            String newId = CompanyProcessToolConst.ELEMENT_ + task.getId();
            content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
            workflow.setBpmnContent(content);
        }
        helper.addElement(snapshotTask.getId(), task);
        return task;
    }

    public SnapshotTask getSnapshotTaskById(long id) {
        Optional<SnapshotTask> taskData = snapshotTaskRepository.findById(id);
        return taskData.orElse(null);
    }
}
