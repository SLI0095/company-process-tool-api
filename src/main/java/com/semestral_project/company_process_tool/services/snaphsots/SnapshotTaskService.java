package com.semestral_project.company_process_tool.services.snaphsots;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.snapshots.*;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotRasciRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotTaskRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotTaskStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

        //All inputs
        var inputs = snapshot.getMandatoryInputs();
        for(WorkItem workItem : original.getMandatoryInputs()){
            //Check if was not snapshot already created during snapshotting
            SnapshotWorkItem snapshotWorkItem = helper.getExistingSnapshotWorkItem(workItem.getId());
            if(snapshotWorkItem == null){
                snapshotWorkItem = snapshotWorkItemService.createSnapshot(workItem,snapshotDescription, helper);
            }
            inputs.add(snapshotWorkItem);
        }
        snapshot.setMandatoryInputs(inputs);

        //All outputs
        var outputs = snapshot.getOutputs();
        for(WorkItem workItem : original.getMandatoryInputs()){
            SnapshotWorkItem snapshotWorkItem = helper.getExistingSnapshotWorkItem(workItem.getId());
            if(snapshotWorkItem == null){
                snapshotWorkItem = snapshotWorkItemService.createSnapshot(workItem,snapshotDescription, helper);
            }
            outputs.add(snapshotWorkItem);
        }
        snapshot.setOutputs(outputs);

        snapshot = snapshotTaskRepository.save(snapshot);
        helper.addElement(original.getId(), snapshot);

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
        return snapshot;
    }
}
