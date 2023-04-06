package cz.sli0095.promod.services.snaphsots;

import cz.sli0095.promod.entities.Element;
import cz.sli0095.promod.entities.Role;
import cz.sli0095.promod.entities.WorkItem;
import cz.sli0095.promod.entities.snapshots.SnapshotElement;
import cz.sli0095.promod.entities.snapshots.SnapshotRole;
import cz.sli0095.promod.entities.snapshots.SnapshotWorkItem;

import java.util.HashMap;

public class SnapshotsHelper {

    private final HashMap<Long, SnapshotElement> elementsSnapshots;
    private final HashMap<Long, SnapshotRole> rolesSnapshots;
    private final HashMap<Long, SnapshotWorkItem> workItemsSnapshot;

    private final HashMap<Long, Element> elements;
    private final HashMap<Long, Role> roles;
    private final HashMap<Long, WorkItem> workItems;

    public SnapshotsHelper(){
        elementsSnapshots = new HashMap<>();
        rolesSnapshots = new HashMap<>();
        workItemsSnapshot = new HashMap<>();
        elements = new HashMap<>();
        roles = new HashMap<>();
        workItems = new HashMap<>();
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

    public void addElement(Long snapshotElementId, Element element){
        elements.put(snapshotElementId,element);
    }

    public Element getExistingElement(Long snapshotId){
        return elements.get(snapshotId);
    }

    public void addRole(Long snapshotRoleId, Role role){
        roles.put(snapshotRoleId,role);
    }

    public Role getExistingRole(Long snapshotId){
        return roles.get(snapshotId);
    }

    public void addWorkItem(Long snapshotWorkItemId, WorkItem workItem){
        workItems.put(snapshotWorkItemId,workItem);
    }

    public WorkItem getExistingWorkItem(Long snapshotId){
        return workItems.get(snapshotId);
    }
}
