package com.semestral_project.company_process_tool.services.snaphsots;

import com.semestral_project.company_process_tool.entities.State;
import com.semestral_project.company_process_tool.entities.WorkItem;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotRole;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotState;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotWorkItem;
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

    public SnapshotWorkItem createSnapshot(WorkItem original, String snapshotDescription){
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

        return snapshotWorkItemRepository.save(snapshot);
    }
}
