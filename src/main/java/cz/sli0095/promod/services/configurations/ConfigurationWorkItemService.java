package cz.sli0095.promod.services.configurations;

import cz.sli0095.promod.entities.Project;
import cz.sli0095.promod.entities.State;
import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.entities.WorkItem;
import cz.sli0095.promod.repositories.StateRepository;
import cz.sli0095.promod.repositories.WorkItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationWorkItemService {

    @Autowired
    WorkItemRepository workItemRepository;

    @Autowired
    StateRepository stateRepository;

    public WorkItem createNewConfiguration(WorkItem defaultWorkItem, ConfigurationHelper helper, User user, Project project){
        if(helper == null){
            helper = new ConfigurationHelper();
        }
        WorkItem workItem = new WorkItem();
        workItem.setName(defaultWorkItem.getName());
        workItem.setBriefDescription(defaultWorkItem.getBriefDescription());
        workItem.setMainDescription(defaultWorkItem.getMainDescription());
        workItem.setVersion(defaultWorkItem.getVersion());
        workItem.setChangeDate(defaultWorkItem.getChangeDate());
        workItem.setChangeDescription(defaultWorkItem.getChangeDescription());
        workItem.setPurpose(defaultWorkItem.getPurpose());
        workItem.setKeyConsiderations(defaultWorkItem.getKeyConsiderations());
        workItem.setBriefOutline(defaultWorkItem.getBriefOutline());
        workItem.setNotation(defaultWorkItem.getNotation());
        workItem.setImpactOfNotHaving(defaultWorkItem.getImpactOfNotHaving());
        workItem.setReasonForNotNeeding(defaultWorkItem.getReasonForNotNeeding());
        workItem.setWorkItemType(defaultWorkItem.getWorkItemType());
        workItem.setUrlAddress(defaultWorkItem.getUrlAddress());
        workItem.setTemplateText(defaultWorkItem.getTemplateText());
        workItem.setTemplate(true);
        workItem.setCreatedFrom(defaultWorkItem);

        workItem.setProject(project);
        if(project == null){
            workItem.setOwner(user);
        } else {
            workItem.setOwner(project.getProjectOwner());
        }

        workItem = workItemRepository.save(workItem);

        for (State defaultState : defaultWorkItem.getWorkItemStates()){
            State state = new State();
            state.setStateName(defaultState.getStateName());
            state.setStateDescription(defaultState.stateDescription);
            state.setWorkItem(workItem);
            stateRepository.save(state);
        }
        workItem = workItemRepository.save(workItem);
        helper.addWorkItem(defaultWorkItem.getId(), workItem);
        return workItem;
    }
}
