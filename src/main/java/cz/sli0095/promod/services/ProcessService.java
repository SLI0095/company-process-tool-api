package cz.sli0095.promod.services;

import cz.sli0095.promod.entities.Process;
import cz.sli0095.promod.entities.snapshots.SnapshotElement;
import cz.sli0095.promod.entities.snapshots.SnapshotItem;
import cz.sli0095.promod.entities.snapshots.SnapshotProcess;
import cz.sli0095.promod.repositories.ElementRepository;
import cz.sli0095.promod.repositories.ProcessMetricRepository;
import cz.sli0095.promod.repositories.ProcessRepository;
import cz.sli0095.promod.repositories.WorkItemRepository;
import cz.sli0095.promod.services.configurations.ConfigurationHelper;
import cz.sli0095.promod.services.configurations.ConfigurationProcessService;
import cz.sli0095.promod.services.snaphsots.SnapshotProcessService;
import cz.sli0095.promod.services.snaphsots.SnapshotsHelper;
import cz.sli0095.promod.utils.ItemUsersUtil;
import cz.sli0095.promod.utils.ProcessAndBpmnHolder;
import cz.sli0095.promod.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessService {

    @Autowired
    ProcessRepository processRepository;
    @Autowired
    ElementRepository elementRepository;
    @Autowired
    BPMNparser bpmnParser;
    @Autowired
    ProcessMetricRepository processMetricRepository;
    @Autowired
    TaskService taskService;
    @Autowired
    HTMLGenerator htmlGenerator;
    @Autowired
    SnapshotProcessService snapshotProcessService;
    @Autowired
    UserService userService;
    @Autowired
    UserTypeService userTypeService;
    @Autowired
    ProcessMetricService processMetricService;
    @Autowired
    ElementService elementService;
    @Autowired
    ConfigurationProcessService configurationProcessService;
    @Autowired
    ProjectService projectService;
    @Autowired
    WorkItemRepository workItemRepository;

    public Process fillProcess(Process oldProcess, Process updatedProcess){
        oldProcess.setName(updatedProcess.getName());
        oldProcess.setBriefDescription(updatedProcess.getBriefDescription());
        oldProcess.setMainDescription(updatedProcess.getMainDescription());
        oldProcess.setVersion(updatedProcess.getVersion());
        oldProcess.setChangeDate(updatedProcess.getChangeDate());
        oldProcess.setChangeDescription(updatedProcess.getChangeDescription());
        oldProcess.setPurpose(updatedProcess.getPurpose());
        oldProcess.setScope(updatedProcess.getScope());
        oldProcess.setAlternatives(updatedProcess.getAlternatives());
        oldProcess.setUsageNotes(updatedProcess.getUsageNotes());
        oldProcess.setHowToStaff(updatedProcess.getHowToStaff());
        oldProcess.setKeyConsiderations(updatedProcess.getKeyConsiderations());
        return oldProcess;
    }

    public List<Process> getAllProcesses(){
        try {
            return (List<Process>) processRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean processExists(long id){
        return processRepository.existsById(id);
    }

    public Process getProcessById(long id){
        Optional<Process> processData = processRepository.findById(id);

        return processData.orElse(null);
    }

    public List<Process> getUsableIn(long id){
        Process process = getProcessById(id);
        if(process == null){
            return null;
        }
        return process.getCanBeUsedIn();
    }

    public long addProcess(Process process, long userId) {
        User owner = userService.getUserById(userId);
        if (owner == null) {
            return -1;
        }
        if(process.getProject() != null){
            Project project = projectService.getProjectById(process.getProject().getId());
            if(project == null) {
                return -1;
            }
            if(!ItemUsersUtil.getAllUsersCanEdit(project).contains(owner)){
                return -1;
            }
            process.setOwner(project.getProjectOwner());
        } else {
            process.setOwner(owner);
        }
        process = processRepository.save(process);
        return process.getId();
    }

    public int addAccess(long processId, long whoEdits, UserType getAccess){
        Process process = getProcessById(processId);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(getAccess.getId());
        if(access == null){
            return 5;
        }
        if(process.getHasAccess().contains(access) || process.getOwner() == access){
            return 3; //already has access
        }
        var list = process.getCanEdit();
        if(list.contains(access)){
            list.remove(access);
            process.setCanEdit(list);
        }
        list = process.getHasAccess();
        list.add(access);
        process.setHasAccess(list);
        processRepository.save(process);
        for(Element e : process.getElements())
        {
            if(e.getClass() == Task.class){
                taskService.addAccess(e.getId(), editor.getId(), getAccess);
            } else {
                this.addAccess(e.getId(), editor.getId(), getAccess); //
            }
        }
        return  1; //OK
    }

    public int removeAccess(long processId, long whoEdits, UserType removeAccess){
        Process process = getProcessById(processId);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(removeAccess.getId());
        if(access == null){
            return 5;
        }
        if(!process.getHasAccess().contains(access)){
            return 3; //nothing to remove
        }
        var list = process.getHasAccess();
        list.remove(access);
        process.setHasAccess(list);
        processRepository.save(process);
        return  1; //OK
    }

    public int removeEdit(long processId, long whoEdits, UserType removeEdit){
        Process process = getProcessById(processId);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(removeEdit.getId());
        if(edit == null){
            return 5;
        }
        if(!process.getCanEdit().contains(edit)){
            return 3; //nothing to remove
        }
        var list = process.getCanEdit();
        list.remove(edit);
        process.setCanEdit(list);
        processRepository.save(process);
        return  1; //OK
    }

    public int addEdit(long processId, long whoEdits, UserType getEdit){
        Process process = getProcessById(processId);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(getEdit.getId());
        if(edit == null){
            return 5;
        }
        if(process.getCanEdit().contains(edit) || process.getOwner() == edit){
            return 3; //already has access
        }
        var list = process.getHasAccess();
        if(list.contains(edit)){
            list.remove(edit);
            process.setHasAccess(list);
        }
        list = process.getCanEdit();
        list.add(edit);
        process.setCanEdit(list);
        processRepository.save(process);
        for(Element e : process.getElements())
        {
            if(e.getClass() == Task.class){
                taskService.addEdit(e.getId(), editor.getId(), edit);
            } else {
                this.addEdit(e.getId(), editor.getId(), edit); //
            }
        }
        return  1; //OK
    }

    public void addEditAutomatic(long processId, UserType getEdit){
        Process process = getProcessById(processId);
        if(process == null){
            return; //process not found
        }
        UserType edit = userTypeService.getUserTypeById(getEdit.getId());
        if(edit == null){
            return;
        }
        if(process.getCanEdit().contains(edit) || process.getOwner() == edit){
            return; //already has access
        }
        var list = process.getHasAccess();
        if(list.contains(edit)){
            list.remove(edit);
            process.setHasAccess(list);
        }
        list = process.getCanEdit();
        list.add(edit);
        process.setCanEdit(list);
        processRepository.save(process);
        for(Element e : process.getElements())
        {
            if(e.getClass() == Task.class){
                taskService.addEditAutomatic(e.getId(), edit);
            } else {
                this.addEditAutomatic(e.getId(), edit);
            }
        }
    }

    public void addAccessAutomatic(long processId, UserType getAccess){
        Process process = getProcessById(processId);
        if(process == null){
            return; //process not found
        }
        UserType access = userTypeService.getUserTypeById(getAccess.getId());
        if(access == null){
            return;
        }
        if(process.getHasAccess().contains(access) || process.getOwner() == access){
            return; //already has access
        }
        var list = process.getCanEdit();
        if(list.contains(access)){
            list.remove(access);
            process.setCanEdit(list);
        }
        list = process.getHasAccess();
        list.add(access);
        process.setHasAccess(list);
        processRepository.save(process);
        for(Element e : process.getElements())
        {
            if(e.getClass() == Task.class){
                taskService.addAccessAutomatic(e.getId(), getAccess);
            } else {
                this.addAccessAutomatic(e.getId(), getAccess);
            }
        }
    }

    public int deleteProcessById(long id, long whoEdits){
        Process process = getProcessById(id);
        if (process == null){
            return  2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 3; //cannot edit
        }
        if (!bpmnParser.removeProcessFromAllWorkflows(process)){
            return 3;
        }
        for(Element e : process.getElements()){
            var list = e.getPartOfProcess();
            list.remove(process);
            e.setPartOfProcess(list);
            elementRepository.save(e);
        }
        for(SnapshotElement snapshot : process.getSnapshots()){
            snapshot.setOriginalElement(null);
        }
        for(Element e : process.getUsableElements()){
            var list = e.getCanBeUsedIn();
            list.remove(process);
            e.setCanBeUsedIn(list);
            elementRepository.save(e);
        }
        for(WorkItem w : process.getUsableWorkItems()){
            var list = w.getCanBeUsedInProcesses();
            list.remove(process);
            w.setCanBeUsedInProcesses(list);
            workItemRepository.save(w);
        }
        for(Item i : process.getConfigurations()){
            Process p = (Process) i;
            p.setCreatedFrom(null);
            processRepository.save(p);
        }
        processRepository.delete(process);
        return 1;
    }

    public int updateProcess(long id, Process process, long whoEdits){
        Process mainProcess = getProcessById(id);
        if (mainProcess == null){
            return  2;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainProcess).contains(editor)){
            return 3;
        }
        mainProcess = fillProcess(mainProcess, process);
        processRepository.save(mainProcess);
        bpmnParser.updateProcessInAllWorkflows(mainProcess, true, null);
        return 1;
    }

    public int updateIsTemplate(long id, boolean isTemplate, long whoEdits) {
        Process mainProcess = getProcessById(id);
        if (mainProcess == null) {
            return 2;
        }
        User editor = userService.getUserById(whoEdits);
        if (editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainProcess).contains(editor)) {
            return 3;
        }
        mainProcess.setTemplate(isTemplate);
        processRepository.save(mainProcess);
        return 1;
    }

    public void addElementToProcess(long id, Element element){
        Process process = getProcessById(id);
        if (process == null){
            return; //process not found
        }
        element = elementService.getElementById(element.getId());
        if(element == null){
            return;
        }
        if(element.getPartOfProcess().contains(process)){
            return;
        }
        var isPartOf = element.getPartOfProcess();
        isPartOf.add(process);
        element.setPartOfProcess(isPartOf);

        var orderList = process.getElementsOrder();
        if(!orderList.contains(element.getId())) {
            orderList.add(element.getId());
            process.setElementsOrder(orderList);
        }


        elementRepository.save(process);
        //add access and edit from process to element
        for(UserType u : process.getCanEdit()){
            if(element.getClass() == Task.class){
                taskService.addEditAutomatic(element.getId(), u);
            } else {
                this.addEditAutomatic(element.getId(), u);
            }
        }
        for(UserType u : process.getHasAccess()){
            if(element.getClass() == Task.class){
                taskService.addAccessAutomatic(element.getId(), u);
            } else {
                this.addAccessAutomatic(element.getId(), u);
            }
        }
    }

    public int saveWorkflow(long id, BPMNfile bpmn, long whoEdits){
        Process process = getProcessById(id);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(process.getProject() != null) {
            Project project = projectService.getProjectById(process.getProject().getId());
            if (project == null) {
                return 5;
            }
            if (!ItemUsersUtil.getAllUsersCanEdit(project).contains(editor)) {
                return 3;
            }
        }else if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 3; //cannot edit
        }
        bpmnParser.saveBPMN(bpmn, process, editor);
        return 1;
    }

    public List<Process> getAllUserCanView(long userId, Long projectId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return processRepository.findAllCanUserViewInDefault(user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return processRepository.findAllCanUserView(user, project);
    }

    public List<Process> getAllUserCanEdit(long userId, Long projectId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return processRepository.findAllCanUserEditInDefault(user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return processRepository.findAllCanUserEdit(user, project);
    }

    public List<Process> getAllUserCanViewByTemplate(long userId, boolean isTemplate, Long projectId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return processRepository.findByIsTemplateUserCanViewInDefault(isTemplate,user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return processRepository.findByIsTemplateUserCanView(isTemplate, user, project);
    }


    public boolean addProcessFromFile(ProcessAndBpmnHolder holder, long whoEdits){
        Process newProcess = holder.getProcess();
        long id = this.addProcess(newProcess, whoEdits);
        if(id == -1){
            return false;
        }
        BPMNfile newWorkflow = holder.getBpmn();
        newWorkflow.setBpmnContent(bpmnParser.prepareImportedFile(newWorkflow.getBpmnContent()));
        this.saveWorkflow(id, newWorkflow, whoEdits);
        return true;
    }

    public int addMetric(Long id, ProcessMetric metric, long whoEdits) {
        Process process = getProcessById(id);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 3; //cannot edit
        }
        metric.setProcess(process);
        processMetricRepository.save(metric);

        return 1;
    }

    public int removeMetric(Long id, ProcessMetric metric, long whoEdits) {
        Process process = getProcessById(id);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 3; //cannot edit
        }
        metric = processMetricService.getMetricById(metric.getId());
        if(metric == null){
            return 3;
        }
        if(metric.getProcess().getId() != process.getId()){
            return 4;
        }
        processMetricRepository.delete(metric);
        return 1;

    }

    public void generateHTML(long id, OutputStream stream){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            htmlGenerator.generateHTML(id, stream);
        }
    }
    public int addUsableIn(long processId, long user,  Process process) {
        Process thisProcess = getProcessById(processId);
        if(thisProcess == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(user);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(thisProcess).contains(editor)){
            return 5; //cannot edit
        }
        if (processId == process.getId() ){
            return 5;
        }
        process = getProcessById(process.getId());
        if(!ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5;
        }
        var list =  thisProcess.getCanBeUsedIn();
        if(list.contains(process)){
            return 3;
        }
        list.add(process);
        thisProcess.setCanBeUsedIn(list);
        processRepository.save(thisProcess);
        return 1;
    }

    public int removeUsableIn(long processId, long user,  Process process) {
        Process thisProcess = getProcessById(processId);
        if(thisProcess == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(user);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(thisProcess).contains(editor)){
            return 5; //cannot edit
        }
        process = getProcessById(process.getId());
        if(!ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5;
        }
        var list =  thisProcess.getCanBeUsedIn();
        if(!list.contains(process)){
            return 3;
        }
        list.remove(process);
        thisProcess.setCanBeUsedIn(list);
        processRepository.save(thisProcess);
        return 1;
    }

    public int createSnapshot(Long id, long userId, SnapshotItem detail) {
        Process process = getProcessById(id);
        if(process == null){
            return 2;
        }
        User editor = userService.getUserById(userId);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 3;
        }
        snapshotProcessService.createSnapshot(process, detail, new SnapshotsHelper());
        return 1;
    }

    public Process restoreProcess(long userId, SnapshotProcess snapshot) {
        snapshot = snapshotProcessService.getSnapshotProcessById(snapshot.getId());
        if(snapshot == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        return snapshotProcessService.restoreFromSnapshot(snapshot,new SnapshotsHelper(), null, user);
    }

    public Process revertProcess(long userId, SnapshotProcess snapshot) {
        snapshot = snapshotProcessService.getSnapshotProcessById(snapshot.getId());
        if(snapshot == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        Process process = getProcessById(snapshot.getOriginalId());
        if(process == null){
            return null;
        }
        if(!ItemUsersUtil.getAllUsersCanEdit(process).contains(user)){
            return null;
        }
        return snapshotProcessService.revertFromSnapshot(snapshot,new SnapshotsHelper(), null, user);
    }

    public void deleteAllMetrics(long id){
        Process process = getProcessById(id);
        for(ProcessMetric metric : process.getMetrics()){
            processMetricRepository.delete(metric);
        }
    }

    public int changeElementOrder(long processId, long user,  List<Long> order) {
        Process process = getProcessById(processId);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(user);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5; //cannot edit
        }
        var list =  process.getElementsOrder();
        if(list.size() != order.size()){
            return 3;
        }
        process.setElementsOrder(order);
        processRepository.save(process);
        return 1;
    }

    public Process createNewConfiguration(long userId, long processId, long projectId) {
        Process process = getProcessById(processId);
        if(process == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        if(projectId == -1){
            return configurationProcessService.createNewConfiguration(process, new ConfigurationHelper(), null, user, null);
        }
        Project project = projectService.getProjectById(projectId);
        if(project != null && ItemUsersUtil.getAllUsersCanEdit(project).contains(user)){
            return configurationProcessService.createNewConfiguration(process, new ConfigurationHelper(), null, user, project);
        }
        return null;
    }

    public int changeOwner(long id, long editorId, long newOwnerId){
        Process process = getProcessById(id);
        if(process == null){
            return 2;
        }
        User editor = userService.getUserById(editorId);
        if(editor == null){
            return 3;
        }
        User newOwner = userService.getUserById(newOwnerId);
        if(newOwner == null){
            return 3;
        }
        if(process.getOwner().getId() != editor.getId()){
            return 4; //MUST BE OWNER TO CHANGE OWNER
        }
        process.setOwner(newOwner);
        processRepository.save(process);
        return 1;
    }
}
