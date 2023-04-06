package cz.sli0095.promod.services.configurations;

import cz.sli0095.promod.entities.WorkItem;
import cz.sli0095.promod.entities.Element;
import cz.sli0095.promod.entities.Role;

import java.util.HashMap;

public class ConfigurationHelper {
    private final HashMap<Long, Element> elements;
    private final HashMap<Long, Role> roles;
    private final HashMap<Long, WorkItem> workItems;

    public ConfigurationHelper() {
        elements = new HashMap<>();
        roles = new HashMap<>();
        workItems = new HashMap<>();
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
