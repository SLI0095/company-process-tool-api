package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProcessRepository processRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BPMNfileRepository bpmnFileRepository;
    @Autowired
    ElementRepository elementRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProcessService processService;
    @Autowired
    RoleService roleService;
    @Autowired
    WorkItemService workItemService;
    @Autowired
    TaskService taskService;


    private Project fillProject(Project oldProject, Project updatedProject){
        oldProject.setName(updatedProject.getName());
        oldProject.setBriefDescription(updatedProject.getBriefDescription());
        oldProject.setMainDescription(updatedProject.getMainDescription());
        oldProject.setVersion(updatedProject.getVersion());
        oldProject.setChangeDate(updatedProject.getChangeDate());
        oldProject.setChangeDescription(updatedProject.getChangeDescription());
        return oldProject;
    }

    public List<Project> getAllProjects(){
        try {
            return (List<Project>) projectRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Project> getAllProjectsForUser(long userId){
        try {
            if(userRepository.existsById(userId)){
                User user = userRepository.findById(userId).get();
                List<Project> allProjects = projectRepository.findAllProjectsForUser(user);
                return allProjects;
            }else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Project getProjectById(long id){
        Optional<Project> projectData = projectRepository.findById(id);

        if(projectData.isPresent()) {
            return projectData.get();
        }
        else return null;
    }

    public long addProject(Project project, long userId){
        try {
            if(userRepository.existsById(userId)){
                User user = userRepository.findById(userId).get();
                var list = project.getCanEdit();
                list.add(user);
                project.setCanEdit(list);
                project = projectRepository.save(project);
                project.getId();
            }
            return -1;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public int deleteProject(long id, long whoEdits){
        Optional<Project> projectData = projectRepository.findById(id);
        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(project_.getCanEdit().contains(whoEdits_)){
                projectRepository.deleteById(id);
                return 1;
            } else {
                return 3; //cannot edit
            }
        }
        else
        {
            return 2;
        }
    }


    public int updateProject(long id, Project project, long whoEdits){
        Optional<Project> projectData = projectRepository.findById(id);
        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(project_.getCanEdit().contains(whoEdits_)){
                project_ = fillProject(project_, project);
                projectRepository.save(project_);
                return 1;
            } else {
                return 3; //cannot edit
            }
        }
        else
        {
            return 2;
        }
    }

    public int addAccess(long projectId, long whoEdits, User getAccess){
        Optional<Project> projectData = projectRepository.findById(projectId);
        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(project_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(getAccess.getId()).get();
               if(project_.getHasAccess().contains(getAccess_)) {
                    return 3; //already has access
                }
                if(project_.getCanEdit().contains(getAccess_)){
                    var list = project_.getCanEdit();
                    list.remove(getAccess_);
                    project_.setCanEdit(list);
                }
                    var list = project_.getHasAccess();
                    list.add(getAccess_);
                    project_.setHasAccess(list);
                    projectRepository.save(project_);
                    return 1; //OK

            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int removeAccess(long projectId, long whoEdits, User removeAccess){
        Optional<Project> projectData = projectRepository.findById(projectId);
        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(project_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(removeAccess.getId()).get();
                if(project_.getHasAccess().contains(getAccess_)) {
                    var list = project_.getHasAccess();
                    list.remove(getAccess_);
                    project_.setHasAccess(list);
                    projectRepository.save(project_);
                    return 1; //access removed
                } else{
                    return 3; //nothing to remove
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int removeEdit(long projectId, long whoEdits, User removeEdit){
        Optional<Project> projectData = projectRepository.findById(projectId);
        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(project_.getCanEdit().contains(whoEdits_)){
                User removeEdit_ = userRepository.findById(removeEdit.getId()).get();
                if(project_.getCanEdit().contains(removeEdit_)) {
                    var list = project_.getCanEdit();
                    list.remove(removeEdit_);
                    project_.setCanEdit(list);
                    projectRepository.save(project_);
                    return 1; //edit removed
                } else{
                    return 3; //nothing to remove
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int addEdit(long projectId, long whoEdits, User getEdit){
        Optional<Project> projectData = projectRepository.findById(projectId);
        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(project_.getCanEdit().contains(whoEdits_)){
                User getEdit_ = userRepository.findById(getEdit.getId()).get();
                if(project_.getCanEdit().contains(getEdit_)){
                    return 4; //already can edit
                } else if(project_.getHasAccess().contains(getEdit_)) {
                    var list = project_.getHasAccess();
                    list.remove(getEdit_);
                    project_.setHasAccess(list);
                    list = project_.getCanEdit();
                    list.add(getEdit_);
                    project_.setCanEdit(list);
                    projectRepository.save(project_);
                    return 1; //OK
                } else{
                    var list = project_.getCanEdit();
                    list.add(getEdit_);
                    project_.setCanEdit(list);
                    projectRepository.save(project_);
                    return 1; //OK
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public List<Element> getAllElementsInProjectForUser(long projectId, long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            List<Element> allElements = elementRepository.findAllElementsInProjectForUser(projectId, user);
            return allElements;
        }
        else return null;

    }

    public List<WorkItem> getAllWorkItemInProjectForUser(long projectId, long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            List<WorkItem> allWorkItems = workItemRepository.findAllWorkItemsInProjectForUser(projectId, user);
            return allWorkItems;
        }
        else return null;
    }

    public List<Role> getAllRolesInProjectForUser(long projectId, long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            List<Role> allRoles = roleRepository.findAllRolesInProjectForUser(projectId, user);
            return allRoles;
        }
        else return null;
    }
    public List<Process> getAllProcessesInProjectForUser(long projectId, long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            List<Process> allProcesses = processRepository.findAllProcessesInProjectForUser(projectId, user);
            return allProcesses;
        }
        else return null;
    }

    public List<Task> getAllTasksInProjectForUser(long projectId, long userId){
        if(userRepository.existsById(userId)){
            User user = userRepository.findById(userId).get();
            return taskRepository.findAllTasksInProjectForUser(projectId, user);
        }
        else return null;
    }


//    private long importingProcess(Project project, Process processTemplate){
//
//        //TODO: fix elements, roles and work items in project not updating
//
//        Process newProcess = new Process();
//        newProcess = processService.fillProcess(newProcess,processTemplate);
//        newProcess.setProject(project);
//        newProcess.setPreviousId(processTemplate.getId());
//        newProcess = processRepository.save(newProcess);
//        project = projectRepository.findById(project.getId()).get();
//        boolean noWorkfow = false;
//
//        if(processTemplate.getWorkflow() == null){
//            noWorkfow = true;
//        }
//
//        String xmlContent;
//        if(noWorkfow){
//            xmlContent = "";
//        } else {
//            xmlContent = processTemplate.getWorkflow().getBpmnContent();
//        }
//
//        for(Element e : processTemplate.getElements()){
//            if(e.getClass() == Task.class){
//                project = projectRepository.findById(project.getId()).get();
//                Task newTask = null;
//                for(Element eInProject : project.getElements()){
//                    if(eInProject.getPreviousId() == e.getId()){
//                        newTask = (Task)eInProject;
//                    }
//                }
//                if(newTask == null) { //task is not in project yet
//
//                    //fill info from template and save
//                    newTask = new Task();
//                    newTask = taskService.fillTask(newTask, (Task) e);
//                    newTask.setProject(project);
//                    newTask.setPreviousId(e.getId());
//                    newTask = taskRepository.save(newTask);
//
//                    //add steps
//                    for(TaskStep step : ((Task) e).getSteps()){
//                        TaskStep newStep = new TaskStep();
//                        newStep.setName(step.getName());
//                        newStep.setDescription(step.getDescription());
//                        taskService.addTaskStep(newTask.getId(),newStep);
//                    }
//
//                    //check all inputs of task
//                    for (WorkItem w : ((Task) e).getMandatoryInputs()) {
//                        WorkItem newWorkItem = null;
//                        project = projectRepository.findById(project.getId()).get();
//                        for (WorkItem wInProject : project.getWorkItems()) {
//                            if (wInProject.getPreviousId() == w.getId()) {
//                                newWorkItem = wInProject;
//                            }
//                        }
//                        if (newWorkItem == null) { //work item is not in project yet
//
//                            //fill info from template and save
//                            newWorkItem = new WorkItem();
//                            newWorkItem = workItemService.fillWorkItem(newWorkItem, w);
//                            newWorkItem.setProject(project);
//                            newWorkItem.setPreviousId(w.getId());
//                            newWorkItem = workItemRepository.save(newWorkItem);
//
//                            //add states as in template and save
//                            for(State s: w.getWorkItemStates()){
//                                State newState = new State();
//                                newState.setStateName(s.getStateName());
//                                newState.setStateDescription(s.getStateDescription());
//                                workItemService.addWorkItemState(newWorkItem.getId(), newState);
//                            }
//                            taskService.addMandatoryInput(newTask.getId(),newWorkItem);
//                        }
//                        //update id in workflow of process
//                        xmlContent = xmlContent.replaceAll("WorkItem_" + w.getId() +"_", "WorkItem_" + newWorkItem.getId() + "_");
//                    }
//
//                    //as previous but outputs are imported
//                    for (WorkItem w : ((Task) e).getOutputs()) {
//                        WorkItem newWorkItem = null;
//                        project = projectRepository.findById(project.getId()).get();
//                        for (WorkItem wInProject : project.getWorkItems()) {
//                            if (wInProject.getPreviousId() == w.getId()) {
//                                newWorkItem = wInProject;
//                            }
//                        }
//                        if (newWorkItem == null) {
//                            newWorkItem = new WorkItem();
//                            newWorkItem = workItemService.fillWorkItem(newWorkItem, w);
//                            newWorkItem.setProject(project);
//                            newWorkItem.setPreviousId(w.getId());
//                            newWorkItem = workItemRepository.save(newWorkItem);
//                            for(State s: w.getWorkItemStates()){
//                                State newState = new State();
//                                newState.setStateName(s.getStateName());
//                                newState.setStateDescription(s.getStateDescription());
//                                workItemService.addWorkItemState(newWorkItem.getId(), newState);
//                            }
//                            taskService.addOutput(newTask.getId(),newWorkItem);
//                        }
//                        xmlContent = xmlContent.replaceAll("WorkItem_" + w.getId() +"_", "WorkItem_" + newWorkItem.getId() + "_");
//                    }
//
//                    //as previous but guidance are imported
//                    for (WorkItem w : ((Task) e).getGuidanceWorkItems()) {
//                        WorkItem newWorkItem = null;
//                        project = projectRepository.findById(project.getId()).get();
//                        for (WorkItem wInProject : project.getWorkItems()) {
//                            if (wInProject.getPreviousId() == w.getId()) {
//                                newWorkItem = wInProject;
//                            }
//                        }
//                        if (newWorkItem == null) {
//                            newWorkItem = new WorkItem();
//                            newWorkItem = workItemService.fillWorkItem(newWorkItem, w);
//                            newWorkItem.setProject(project);
//                            newWorkItem.setPreviousId(w.getId());
//                            newWorkItem = workItemRepository.save(newWorkItem);
//                            for(State s: w.getWorkItemStates()){
//                                State newState = new State();
//                                newState.setStateName(s.getStateName());
//                                newState.setStateDescription(s.getStateDescription());
//                                workItemService.addWorkItemState(newWorkItem.getId(), newState);
//                            }
//                            taskService.addGuidanceWorkItem(newTask.getId(),newWorkItem);
//                        }
//                        xmlContent = xmlContent.replaceAll("WorkItem_" + w.getId() +"_", "WorkItem_" + newWorkItem.getId() + "_");
//                    }
//
//                    //roles and rasci
//                    for(Rasci rasci : ((Task) e).getRasciList()){
//                        Role newRole = null;
//                        Role role = rasci.getRole();
//                        project = projectRepository.findById(project.getId()).get();
//                        for (Role rInProject : project.getRoles()) { //check if role is already in project
//                            if (rInProject.getPreviousId() == role.getId()) {
//                                newRole = rInProject;
//                            }
//                        }
//                        if(newRole == null){ // role not in project yet
//                            newRole = new Role();
//                            newRole = roleService.fillRole(newRole,role);
//                            newRole.setProject(project);
//                            newRole.setPreviousId(role.getId());
//                            newRole = roleRepository.save(newRole);
//                        }
//                        Rasci newRasci = new Rasci();
//                        newRasci.setRole(newRole);
//                        newRasci.setType(rasci.getType());
//                        taskService.addRasci(newTask.getId(), newRasci);
//                    }
//                }
//                var listOfProcesses = newTask.getPartOfProcess();
//                listOfProcesses.add(newProcess);
//                newTask.setPartOfProcess(listOfProcesses);
//                newTask = taskRepository.save(newTask);
//                xmlContent = xmlContent.replaceAll("Element_" + e.getId() +"_", "Element_" + newTask.getId() + "_");
//            } else { //is Process
//                Process newSubProcess = null;
//                for(Element eInProject : project.getElements()){
//                    if(eInProject.getPreviousId() == e.getId()){
//                        newSubProcess = (Process) eInProject;
//                        var listOfProcesses = newSubProcess.getPartOfProcess();
//                        listOfProcesses.add(newProcess);
//                        newSubProcess.setPartOfProcess(listOfProcesses);
//                        newSubProcess = processRepository.save(newSubProcess);
//                        xmlContent = xmlContent.replaceAll("Element_" + e.getId() +"_", "Element_" + newSubProcess.getId() + "_");
//                    }
//                }
//                if(newSubProcess == null) {
//                    long newId = this.importingProcess(project, (Process) e);
//                    Process p = processRepository.findById(newId).get();
//                    var listOfProcesses = p.getPartOfProcess();
//                    listOfProcesses.add(newProcess);
//                    p.setPartOfProcess(listOfProcesses);
//                    p = processRepository.save(p);
//                    xmlContent = xmlContent.replaceAll("Element_" + e.getId() + "_", "Element_" + newId + "_");
//                }
//            }
//        }
//
//        //save bpmn and return newProcess id
//        if(!noWorkfow){
//            BPMNfile newWorkflow = new BPMNfile();
//            newWorkflow.setProcess(newProcess);
//            newWorkflow.setBpmnContent(xmlContent);
//            newWorkflow = bpmnFileRepository.save(newWorkflow);
//            newProcess.setWorkflow(newWorkflow);
//            processRepository.save(newProcess);
//        }
//        return newProcess.getId();
//    }
//
//    public int importTemplateProcess(long projectId, Process processTemplate){
//
//        Process processToImport = null;
//        Project project = null;
//
//        if(processRepository.existsById(processTemplate.getId())){
//            processToImport = processRepository.findById(processTemplate.getId()).get();
//        }
//
//        if(projectRepository.existsById(projectId)){
//            project = projectRepository.findById(projectId).get();
//        }
//
//        if(processToImport != null && project != null){
//            importingProcess(project, processToImport);
//            return 1;
//        }else
//            return 2;
//    }


}
