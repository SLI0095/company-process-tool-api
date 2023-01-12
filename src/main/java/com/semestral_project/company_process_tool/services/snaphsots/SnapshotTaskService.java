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
import com.semestral_project.company_process_tool.services.TaskService;
import com.semestral_project.company_process_tool.utils.BPMNSnapshotUtil;
import com.semestral_project.company_process_tool.utils.CompanyProcessToolConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    TaskService taskService;

    //@Transactional
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

    //@Transactional
    public Task restoreFromSnapshot(SnapshotTask snapshotTask, SnapshotsHelper helper, BPMNSnapshotUtil workflow, User user){
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
                String content = workflow.toString();
                String originalId = CompanyProcessToolConst.WORKITEM_ + snapshotWorkItem.getOriginalId().toString() + "_";
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId() + "_";
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.changeTo(content);
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
                String content = workflow.toString();
                String originalId = CompanyProcessToolConst.WORKITEM_ + snapshotWorkItem.getOriginalId().toString() + "_";
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId() + "_";
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.changeTo(content);
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
            String content = workflow.toString();
            String originalId = CompanyProcessToolConst.ELEMENT_ + snapshotTask.getOriginalId().toString() + "_";
            String newId = CompanyProcessToolConst.ELEMENT_ + task.getId() + "_";
            content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
            workflow.changeTo(content);
        }
        helper.addElement(snapshotTask.getId(), task);
        return task;
    }

    public SnapshotTask getSnapshotTaskById(long id) {
        Optional<SnapshotTask> taskData = snapshotTaskRepository.findById(id);
        return taskData.orElse(null);
    }

    //@Transactional
    public Task revertExistingFromSnapshot(SnapshotTask snapshotTask, SnapshotsHelper helper, BPMNSnapshotUtil workflow, User user){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        boolean updateWorkflow = workflow.toString() != null;
        Task task = taskService.getTaskById(snapshotTask.getOriginalId());
        String oldTaskType = task.getTaskType();

        task.setName(snapshotTask.getName());
        task.setBriefDescription(snapshotTask.getBriefDescription());
        task.setMainDescription(snapshotTask.getMainDescription());
        task.setVersion(snapshotTask.getVersion());
        task.setChangeDate(snapshotTask.getChangeDate());
        task.setChangeDescription(snapshotTask.getChangeDescription());
        task.setPurpose(snapshotTask.getPurpose());
        task.setKeyConsiderations(snapshotTask.getKeyConsiderations());
        task.setTaskType(snapshotTask.getTaskType());

        task = taskRepository.save(task);

        //All inputs

        // check if work item from snapshot exists - then just revert values based on snapshot - then check if is already as input - if not add
        // if work it does not exist then recreate the new one - in this case always add as input
        // then remove inputs that are not in snapshot
        // PLUS check helper for recreated and reverted work items first
        // if some input is removed need to remove link in workflows - only when not getting any workflow
        // when get workflow update id of workItem when is workItem recreated
        List<WorkItem> allInputs = new ArrayList<>();
        for(SnapshotWorkItem snapshotWorkItem : snapshotTask.getMandatoryInputs()){
            //Check if was not snapshot already created during snapshotting
            boolean changeIdInWorkflow = false;
            WorkItem workItem = helper.getExistingWorkItem(snapshotWorkItem.getId());
            if(workItem == null){
                if(snapshotWorkItemService.existsWorkItem(snapshotWorkItem.getOriginalId())){
                    workItem = snapshotWorkItemService.revertFromSnapshot(snapshotWorkItem, helper);
                } else {
                    workItem = snapshotWorkItemService.restoreFromSnapshot(snapshotWorkItem, helper, user);
                    changeIdInWorkflow = true;
                }
            }
            allInputs.add(workItem);
            if(!task.getMandatoryInputs().contains(workItem)){
                var asInput = workItem.getAsMandatoryInput();
                asInput.add(task);
                workItem.setAsMandatoryInput(asInput);
                workItemRepository.save(workItem);
            }
            if(updateWorkflow && changeIdInWorkflow){
                //Change old id in workflow
                String content = workflow.toString();
                String originalId = CompanyProcessToolConst.WORKITEM_ + snapshotWorkItem.getOriginalId().toString() + "_";
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId() + "_";
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.changeTo(content);
            }
        }
        task = taskService.getTaskById(task.getId());
        for(WorkItem w : task.getMandatoryInputs()){
            if(!allInputs.contains(w)){
                taskService.removeMandatoryInputWithoutUser(task.getId(), w);
            }
        }


        //All outputs
        List<WorkItem> allOutputs = new ArrayList<>();
        for(SnapshotWorkItem snapshotWorkItem : snapshotTask.getOutputs()){
            boolean changeIdInWorkflow = false;
            WorkItem workItem = helper.getExistingWorkItem(snapshotWorkItem.getId());
            if(workItem == null){
                if(snapshotWorkItemService.existsWorkItem(snapshotWorkItem.getOriginalId())){
                    workItem = snapshotWorkItemService.revertFromSnapshot(snapshotWorkItem, helper);
                } else {
                    workItem = snapshotWorkItemService.restoreFromSnapshot(snapshotWorkItem, helper, user);
                    changeIdInWorkflow = true;
                }
            }
            allOutputs.add(workItem);
            if(!task.getOutputs().contains(workItem)){
                var asOutput = workItem.getAsOutput();
                asOutput.add(task);
                workItem.setAsOutput(asOutput);
                workItemRepository.save(workItem);
            }
            if(updateWorkflow && changeIdInWorkflow){
                //Change old id in workflow
                String content = workflow.toString();
                String originalId = CompanyProcessToolConst.WORKITEM_ + snapshotWorkItem.getOriginalId().toString() + "_";
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId() + "_";
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.changeTo(content);
            }
        }
        task = taskService.getTaskById(task.getId());
        for(WorkItem w : task.getOutputs()){
            if(!allOutputs.contains(w)){
                taskService.removeOutputWithoutUser(task.getId(), w);
            }
        }


        //Remove all task steps and then use this to recreate them
        taskService.deleteAllSteps(task.getId());
        for(SnapshotTaskStep snapshotStep : snapshotTask.getSteps()){
            TaskStep taskStep = new TaskStep();
            taskStep.setName(snapshotStep.getName());
            taskStep.setDescription(snapshotStep.getDescription());
            taskStep.setTask(task);
            taskStepRepository.save(taskStep);
        }

        //Check existence of role - revert or recreate
        //Need to found rasci based on role id then if necessary change type
        //Also remove rasci that are not in re

        for(SnapshotRasci snapshotRasci : snapshotTask.getRasciList()){
            SnapshotRole snapshotRole = snapshotRasci.getRole();
            Role role = helper.getExistingRole(snapshotRole.getId());
            if(role == null) {
                if (snapshotRoleService.existRole(snapshotRole.getId())) {
                    role = snapshotRoleService.revertRoleFromSnapshot(snapshotRole, helper);
                } else {
                    role = snapshotRoleService.restoreRoleFromSnapshot(snapshotRole, helper, user);
                }
            }
            //delete all rasci from task
            taskService.deleteAllRasci(task.getId());
            Rasci rasci = new Rasci();
            rasci.setType(snapshotRasci.getType());
            rasci.setRole(role);
            rasci.setTask(task);
            rasciRepository.save(rasci);
        }
        boolean typeChanged = !oldTaskType.equals(snapshotTask.getTaskType());
        bpmNparser.updateTaskInAllWorkflows(task, true, typeChanged, oldTaskType,null);
        helper.addElement(snapshotTask.getId(), task);
        return task;
    }

   // @Transactional
    public Task revertNonExistingFromSnapshot(SnapshotTask snapshotTask, SnapshotsHelper helper, BPMNSnapshotUtil workflow, User user){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        boolean updateWorkflow = workflow.toString() != null;
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

        // check if work item from snapshot exists - then just revert values based on snapshot - then check if is already as input - if not add
        // if work it does not exist then recreate the new one - in this case always add as input
        // then remove inputs that are not in snapshot
        // PLUS check helper for recreated and reverted work items first
        // if some input is removed need to remove link in workflows - only when not getting any workflow
        // when get workflow update id of workItem when is workItem recreated
        for(SnapshotWorkItem snapshotWorkItem : snapshotTask.getMandatoryInputs()){
            //Check if was not snapshot already created during snapshotting
            boolean changeIdInWorkflow = false;
            WorkItem workItem = helper.getExistingWorkItem(snapshotWorkItem.getId());
            if(workItem == null){
                if(snapshotWorkItemService.existsWorkItem(snapshotWorkItem.getOriginalId())){
                    workItem = snapshotWorkItemService.revertFromSnapshot(snapshotWorkItem, helper);
                } else {
                    workItem = snapshotWorkItemService.restoreFromSnapshot(snapshotWorkItem, helper, user);
                    changeIdInWorkflow = true;
                }
            }

            var asInput = workItem.getAsMandatoryInput();
            asInput.add(task);
            workItem.setAsMandatoryInput(asInput);
            workItemRepository.save(workItem);

            if(updateWorkflow && changeIdInWorkflow){
                //Change old id in workflow
                String content = workflow.toString();
                String originalId = CompanyProcessToolConst.WORKITEM_ + snapshotWorkItem.getOriginalId().toString() + "_";
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId() + "_";
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.changeTo(content);
            }
        }

        //All outputs
        for(SnapshotWorkItem snapshotWorkItem : snapshotTask.getOutputs()){
            boolean changeIdInWorkflow = false;
            WorkItem workItem = helper.getExistingWorkItem(snapshotWorkItem.getId());
            if(workItem == null){
                if(snapshotWorkItemService.existsWorkItem(snapshotWorkItem.getOriginalId())){
                    workItem = snapshotWorkItemService.revertFromSnapshot(snapshotWorkItem, helper);
                } else {
                    workItem = snapshotWorkItemService.restoreFromSnapshot(snapshotWorkItem, helper, user);
                    changeIdInWorkflow = true;
                }
            }
            var asOutput = workItem.getAsOutput();
            asOutput.add(task);
            workItem.setAsOutput(asOutput);
            workItemRepository.save(workItem);

            if(updateWorkflow && changeIdInWorkflow){
                //Change old id in workflow
                String content = workflow.toString();
                String originalId = CompanyProcessToolConst.WORKITEM_ + snapshotWorkItem.getOriginalId().toString() + "_";
                String newId = CompanyProcessToolConst.WORKITEM_ + workItem.getId() + "_";
                content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                workflow.changeTo(content);
            }
        }

        for(SnapshotTaskStep snapshotStep : snapshotTask.getSteps()){
            TaskStep taskStep = new TaskStep();
            taskStep.setName(snapshotStep.getName());
            taskStep.setDescription(snapshotStep.getDescription());
            taskStep.setTask(task);
            taskStepRepository.save(taskStep);
        }

        //Check existence of role - revert or recreate
        //Need to found rasci based on role id then if necessary change type
        //Also remove rasci that are not in re

        for(SnapshotRasci snapshotRasci : snapshotTask.getRasciList()){
            SnapshotRole snapshotRole = snapshotRasci.getRole();
            Role role = helper.getExistingRole(snapshotRole.getId());
            if(role == null) {
                if (snapshotRoleService.existRole(snapshotRole.getId())) {
                    role = snapshotRoleService.revertRoleFromSnapshot(snapshotRole, helper);
                } else {
                    role = snapshotRoleService.restoreRoleFromSnapshot(snapshotRole, helper, user);
                }
            }
            Rasci rasci = new Rasci();
            rasci.setType(snapshotRasci.getType());
            rasci.setRole(role);
            rasci.setTask(task);
            rasciRepository.save(rasci);
        }
        if(updateWorkflow){
            //Change old id in workflow
            String content = workflow.toString();
            String originalId = CompanyProcessToolConst.ELEMENT_ + snapshotTask.getOriginalId().toString() + "_";
            String newId = CompanyProcessToolConst.ELEMENT_ + task.getId()  + "_";
            content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
            workflow.changeTo(content);
        }
        helper.addElement(snapshotTask.getId(), task);
        return task;
    }

    public boolean existsTask(long id){
        return taskRepository.existsById(id);
    }
}
