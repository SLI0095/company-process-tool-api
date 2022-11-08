package com.semestral_project.company_process_tool.services.snaphsots;

import com.semestral_project.company_process_tool.entities.snapshots.SnapshotElement;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotRole;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotWorkItem;

import java.util.HashMap;

public class SnapshotsHelper {

    private HashMap<Long, SnapshotElement> elementsSnapshots;
    private HashMap<Long, SnapshotRole> rolesSnapshots;
    private HashMap<Long, SnapshotWorkItem> workItemsSnapshot;

    public SnapshotsHelper(){
        elementsSnapshots = new HashMap<>();
        rolesSnapshots = new HashMap<>();
        workItemsSnapshot = new HashMap<>();
    }

    public void addElement(Long elementId, SnapshotElement snapshot){
        elementsSnapshots.put(elementId,snapshot);
    }

    public SnapshotElement getExistingSnapshotElement(Long elementId){
        return elementsSnapshots.get(elementId);
    }

    public void addRole(Long roleId, SnapshotRole snapshot){
        rolesSnapshots.put(roleId,snapshot);
    }

    public SnapshotRole getExistingSnapshotRole(Long roleId){
        return rolesSnapshots.get(roleId);
    }

    public void addWorkItem(Long workItemId, SnapshotWorkItem snapshot){
        workItemsSnapshot.put(workItemId,snapshot);
    }

    public SnapshotWorkItem getExistingSnapshotWorkItem(Long workItemId){
        return workItemsSnapshot.get(workItemId);
    }
}
