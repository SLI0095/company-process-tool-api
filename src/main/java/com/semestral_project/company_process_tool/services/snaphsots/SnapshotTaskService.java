package com.semestral_project.company_process_tool.services.snaphsots;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.snapshots.*;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotRasciRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotTaskRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotTaskStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public SnapshotTask createSnapshot(Task original, String snapshotDescription){
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

        //snapshot = snapshotTaskRepository.save(snapshot);

        var inputs = snapshot.getMandatoryInputs();
        for(WorkItem workItem : original.getMandatoryInputs()){
            SnapshotWorkItem snapshotWorkItem = snapshotWorkItemService.createSnapshot(workItem,snapshotDescription);
            inputs.add(snapshotWorkItem);
        }
        snapshot.setMandatoryInputs(inputs);

        var outputs = snapshot.getOutputs();
        for(WorkItem workItem : original.getMandatoryInputs()){
            SnapshotWorkItem snapshotWorkItem = snapshotWorkItemService.createSnapshot(workItem,snapshotDescription);
            outputs.add(snapshotWorkItem);
        }
        snapshot.setOutputs(outputs);

        snapshot = snapshotTaskRepository.save(snapshot);

        for(TaskStep step : original.getSteps()){
            SnapshotTaskStep snapshotTaskStep = new SnapshotTaskStep();
            snapshotTaskStep.setName(step.getName());
            snapshotTaskStep.setDescription(step.getDescription());
            snapshotTaskStep.setTask(snapshot);
            snapshotTaskStepRepository.save(snapshotTaskStep);
        }

        for(Rasci rasci : original.getRasciList()){
            Role role = rasci.getRole();
            SnapshotRole snapshotRole = snapshotRoleService.createSnapshotRole(role, snapshotDescription);
            SnapshotRasci snapshotRasci = new SnapshotRasci();
            snapshotRasci.setType(rasci.getType());
            snapshotRasci.setRole(snapshotRole);
            snapshotRasci.setTask(snapshot);
            snapshotRasciRepository.save(snapshotRasci);
        }
        return snapshot;
    }
}
