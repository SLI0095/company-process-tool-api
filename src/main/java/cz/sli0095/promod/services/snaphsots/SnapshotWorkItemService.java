package cz.sli0095.promod.services.snaphsots;

import cz.sli0095.promod.entities.State;
import cz.sli0095.promod.entities.WorkItem;
import cz.sli0095.promod.entities.snapshots.SnapshotItem;
import cz.sli0095.promod.repositories.snapshots.SnapshotStateRepository;
import cz.sli0095.promod.repositories.snapshots.SnapshotWorkItemRepository;
import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.entities.snapshots.SnapshotState;
import cz.sli0095.promod.entities.snapshots.SnapshotWorkItem;
import cz.sli0095.promod.repositories.StateRepository;
import cz.sli0095.promod.repositories.WorkItemRepository;
import cz.sli0095.promod.services.BPMNparser;
import cz.sli0095.promod.services.WorkItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SnapshotWorkItemService {

    @Autowired
    SnapshotWorkItemRepository snapshotWorkItemRepository;
    @Autowired
    SnapshotStateRepository snapshotStateRepository;
    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    WorkItemService workItemService;
    @Autowired
    StateRepository stateRepository;
    @Autowired
    BPMNparser bpmNparser;

    public SnapshotWorkItem createSnapshot(WorkItem original, SnapshotItem snapshotDetail, SnapshotsHelper helper){
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

        snapshot.setSnapshotName(snapshotDetail.getSnapshotName());
        snapshot.setSnapshotDescription(snapshotDetail.getSnapshotDescription());
        snapshot.setSnapshotDate(LocalDate.now());
        snapshot.setOriginalWorkItem(original);
        snapshot.setOriginalId(original.getId());

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

    public WorkItem restoreFromSnapshot(SnapshotWorkItem snapshotWorkItem, SnapshotsHelper helper, User user){
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

        workItem.setOwner(user);

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

    public WorkItem revertFromSnapshot(SnapshotWorkItem snapshotWorkItem, SnapshotsHelper helper){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        WorkItem workItem = workItemService.getWorkItemById(snapshotWorkItem.getOriginalId());
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

        workItemService.deleteAllStates(workItem.getId());
        for (SnapshotState snapshotState : snapshotWorkItem.getWorkItemStates()){
            State state = new State();
            state.setStateName(snapshotState.getStateName());
            state.setStateDescription(snapshotState.stateDescription);
            state.setWorkItem(workItem);
            stateRepository.save(state);
        }
        bpmNparser.updateWorkItemInAllWorkflows(workItem, true, null);
        helper.addWorkItem(snapshotWorkItem.getId(), workItem);
        return workItem;
    }

    public SnapshotWorkItem getSnapshotWorkItemById(long id) {
        Optional<SnapshotWorkItem> workItemData = snapshotWorkItemRepository.findById(id);
        return workItemData.orElse(null);
    }

    public boolean existsWorkItem(long id){
        return workItemRepository.existsById(id);
    }
}
