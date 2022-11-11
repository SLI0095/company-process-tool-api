package com.semestral_project.company_process_tool.services.snaphsots;

import com.semestral_project.company_process_tool.entities.State;
import com.semestral_project.company_process_tool.entities.WorkItem;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotState;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotWorkItem;
import com.semestral_project.company_process_tool.repositories.StateRepository;
import com.semestral_project.company_process_tool.repositories.WorkItemRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotStateRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotWorkItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SnapshotWorkItemService {

    @Autowired
    SnapshotWorkItemRepository snapshotWorkItemRepository;
    @Autowired
    SnapshotStateRepository snapshotStateRepository;
    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    StateRepository stateRepository;

    public SnapshotWorkItem createSnapshot(WorkItem original, String snapshotDescription, SnapshotsHelper helper){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        SnapshotWorkItem snapshot = new SnapshotWorkItem();
        snapshot.setName(original.getName());
        snapshot.setBriefDescription(original.getBriefDescription());
        snapshot.setMainDescription(original.getMainDescription());
        snapshot.setVersion(original.getVersion());
        snapshot.setChangeDate(original.getChangeDate());
        snapshot.setChangeDescription(original.getChangeDescription());
        snapshot.setPurpose(original.getPurpose());
        snapshot.setKeyConsiderations(original.getKeyConsiderations());
        snapshot.setBriefOutline(original.getBriefOutline());
        snapshot.setNotation(original.getNotation());
        snapshot.setImpactOfNotHaving(original.getImpactOfNotHaving());
        snapshot.setReasonForNotNeeding(original.getReasonForNotNeeding());
        snapshot.setWorkItemType(original.getWorkItemType());
        snapshot.setUrlAddress(original.getUrlAddress());
        snapshot.setTemplateText(original.getTemplateText());

        snapshot.setSnapshotDescription(snapshotDescription);
        snapshot.setSnapshotDate(LocalDate.now());
        snapshot.setOriginalWorkItem(original);

        snapshot = snapshotWorkItemRepository.save(snapshot);

        for(State state : original.getWorkItemStates()){
            SnapshotState snapshotState = new SnapshotState();
            snapshotState.setStateName(state.getStateName());
            snapshotState.setStateDescription(state.getStateDescription());
            snapshotState.setWorkItem(snapshot);
            snapshotStateRepository.save(snapshotState);
        }

        snapshot = snapshotWorkItemRepository.save(snapshot);
        helper.addWorkItem(original.getId(), snapshot);
        return snapshotWorkItemRepository.save(snapshot);
    }
    
    public WorkItem restoreFromSnapshot(SnapshotWorkItem snapshotWorkItem, SnapshotsHelper helper){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        WorkItem workItem = new WorkItem();
        workItem.setName(snapshotWorkItem.getName());
        workItem.setBriefDescription(snapshotWorkItem.getBriefDescription());
        workItem.setMainDescription(snapshotWorkItem.getMainDescription());
        workItem.setVersion(snapshotWorkItem.getVersion());
        workItem.setChangeDate(snapshotWorkItem.getChangeDate());
        workItem.setChangeDescription(snapshotWorkItem.getChangeDescription());
        workItem.setPurpose(snapshotWorkItem.getPurpose());
        workItem.setKeyConsiderations(snapshotWorkItem.getKeyConsiderations());
        workItem.setBriefOutline(snapshotWorkItem.getBriefOutline());
        workItem.setNotation(snapshotWorkItem.getNotation());
        workItem.setImpactOfNotHaving(snapshotWorkItem.getImpactOfNotHaving());
        workItem.setReasonForNotNeeding(snapshotWorkItem.getReasonForNotNeeding());
        workItem.setWorkItemType(snapshotWorkItem.getWorkItemType());
        workItem.setUrlAddress(snapshotWorkItem.getUrlAddress());
        workItem.setTemplateText(snapshotWorkItem.getTemplateText());

        workItem = workItemRepository.save(workItem);

        for (SnapshotState snapshotState : snapshotWorkItem.getWorkItemStates()){
            State state = new State();
            state.setStateName(snapshotState.getStateName());
            state.setStateDescription(snapshotState.stateDescription);
            state.setWorkItem(workItem);
            stateRepository.save(state);
        }
        workItem = workItemRepository.save(workItem);
        helper.addWorkItem(snapshotWorkItem.getId(), workItem);
        return workItem;
    }
}
