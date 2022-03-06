package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.*;
import com.semestral_project.company_process_tool.utils.ProcessAndBpmnHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    BPMNfileRepository bpmNfileRepository;
    @Autowired
    ProcessMetricRepository processMetricRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectService projectService;
    @Autowired
    TaskService taskService;
    @Autowired
    TaskRepository taskRepository;

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

    public Process getProcessById(long id){
        Optional<Process> processData = processRepository.findById(id);

        if(processData.isPresent()) {
            return processData.get();
        }
        else return null;
    }

    public long addProcess(Process process, long userId){
        try {
            if(userRepository.existsById(userId)) {
                User user = userRepository.findById(userId).get();
                if(process.getProject() != null){
                    Project project = projectService.getProjectById(process.getProject().getId());
                    if(project.getCanEdit().contains(user)){
                        var list = process.getCanEdit();
                        list.add(user);
                        process = processRepository.save(process);
                        return process.getId();
                    } else {
                        return -1;
                    }
                }
                var list = process.getCanEdit();
                list.add(user);
                process = processRepository.save(process);
                return process.getId();
            }
            else return -1;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }
//
//    public boolean addProcess(Process process){
//        try {
//            processRepository.save(process);
//            return true;
//        }
//        catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//            return false;
//        }
//    }

    public int addAccess(long processId, long whoEdits, User getAccess){
        Optional<Process> processData = processRepository.findById(processId);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(process_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(getAccess.getId()).get();
                if(process_.getHasAccess().contains(getAccess_)) {
                    return 3; //already has access
                }
                if(process_.getCanEdit().contains(getAccess_)){
                    var list = process_.getCanEdit();
                    list.remove(getAccess_);
                    process_.setCanEdit(list);
                }
                var list = process_.getHasAccess();
                list.add(getAccess_);
                process_.setHasAccess(list);
                processRepository.save(process_);
                for(Element e : process_.getElements())
                {
                    if(e.getClass() == Task.class){
                        taskService.addAccess(e.getId(), whoEdits_.getId(),getAccess);
                    } else {
                        this.addAccess(e.getId(),whoEdits_.getId(), getAccess); //
                    }
                }
                return 1; //OK

            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int removeAccess(long processId, long whoEdits, User removeAccess){
        Optional<Process> processData = processRepository.findById(processId);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(process_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(removeAccess.getId()).get();
                if(process_.getHasAccess().contains(getAccess_)) {
                    var list = process_.getHasAccess();
                    list.remove(getAccess_);
                    process_.setHasAccess(list);
                    processRepository.save(process_);
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

    public int removeEdit(long processId, long whoEdits, User removeEdit){
        Optional<Process> processData = processRepository.findById(processId);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(process_.getCanEdit().contains(whoEdits_)){
                User removeEdit_ = userRepository.findById(removeEdit.getId()).get();
                if(process_.getCanEdit().contains(removeEdit_)) {
                    var list = process_.getCanEdit();
                    list.remove(removeEdit_);
                    process_.setCanEdit(list);
                    processRepository.save(process_);
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

    public int addEdit(long processId, long whoEdits, User getEdit){
        Optional<Process> processData = processRepository.findById(processId);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(process_.getCanEdit().contains(whoEdits_)){
                User getEdit_ = userRepository.findById(getEdit.getId()).get();
                if(process_.getCanEdit().contains(getEdit_)){
                    return 4; //already can edit
                } if(process_.getHasAccess().contains(getEdit_)) {
                    var list = process_.getHasAccess();
                    list.remove(getEdit_);
                    process_.setHasAccess(list);
                }
                var list = process_.getCanEdit();
                list.add(getEdit_);
                process_.setCanEdit(list);
                processRepository.save(process_);
                for(Element e : process_.getElements())
                {
                    if(e.getClass() == Task.class){
                        taskService.addEdit(e.getId(), whoEdits_.getId(),getEdit);
                    } else {
                        this.addEdit(e.getId(),whoEdits_.getId(), getEdit); //
                    }
                }
                    return 1; //OK

            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int addEditAutomatic(long processId, User getEdit){
        Optional<Process> processData = processRepository.findById(processId);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            User getEdit_ = userRepository.findById(getEdit.getId()).get();
            if (process_.getCanEdit().contains(getEdit_)) {
                return 4; //already can edit
            }
            if (process_.getHasAccess().contains(getEdit_)) {
                var list = process_.getHasAccess();
                list.remove(getEdit_);
                process_.setHasAccess(list);
            }
            var list = process_.getCanEdit();
            list.add(getEdit_);
            process_.setCanEdit(list);
            processRepository.save(process_);
            for (Element e : process_.getElements()) {
                if (e.getClass() == Task.class) {
                    taskService.addEditAutomatic(e.getId(), getEdit);
                } else {
                    this.addEditAutomatic(e.getId(), getEdit);
                }
            }
            return 1; //OK
        }
        else
        {
            return 2; //role not found
        }
    }

    public int addAccessAutomatic(long processId, User getAccess){
        Optional<Process> processData = processRepository.findById(processId);
        if(processData.isPresent()) {
            Process process_ = processData.get();

            User getAccess_ = userRepository.findById(getAccess.getId()).get();
            if (process_.getHasAccess().contains(getAccess_)) {
                return 3; //already has access
            }
            if (process_.getCanEdit().contains(getAccess_)) {
                var list = process_.getCanEdit();
                list.remove(getAccess_);
                process_.setCanEdit(list);
            }
            var list = process_.getHasAccess();
            list.add(getAccess_);
            process_.setHasAccess(list);
            processRepository.save(process_);
            for (Element e : process_.getElements()) {
                if (e.getClass() == Task.class) {
                    taskService.addAccessAutomatic(e.getId(), getAccess);
                } else {
                    this.addAccessAutomatic(e.getId(), getAccess); //
                }
            }
            return 1; //OK
        }
        else
        {
            return 2; //role not found
        }
    }



    public int deleteProcessById(long id, long whoEdits){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            if (bpmnParser.removeProcessFromAllWorkflows(process_)) {
                processRepository.deleteById(id);
                return 1;
            }
            return 3;
        }
        return 2;
    }

    public int updateProcess(long id, Process process, long whoEdits){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()){
            Process process_ = processData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(process_.getCanEdit().contains(whoEdits_)) {
                process_ = fillProcess(process_, process);
                processRepository.save(process_);
                bpmnParser.updateProcessInAllWorkflows(process_, true, null);
                return 1;
            }
            return 3;
        }
        else
        {
            return 2;
        }
    }

    public int addElementToProcess(long id, Element element){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            Element element_ = elementRepository.findById(element.getId()).get();
            var isPartOf = element_.getPartOfProcess();
            if(isPartOf.contains(process_))
            {
                return 3;
            }
            isPartOf.add(process_);
            element_.setPartOfProcess(isPartOf);
            elementRepository.save(element_);
            //add access and edit from process to element
            for(User u : process_.getCanEdit()){
                if(element_.getClass() == Task.class){
                    taskService.addEditAutomatic(element_.getId(), u);
                } else {
                    this.addEditAutomatic(element_.getId(), u);
                }
            }
            for(User u : process_.getHasAccess()){
                if(element_.getClass() == Task.class){
                    taskService.addAccessAutomatic(element_.getId(), u);
                } else {
                    this.addAccessAutomatic(element_.getId(), u);
                }
            }
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int removeElementFromProcess(long id, Element element){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            Element element_ = elementRepository.findById(element.getId()).get();
            var elementList = process_.getElements();
            if(elementList.contains(element_)) {
                elementList.remove(element_);
                process_.setElements(elementList);
                processRepository.save(process_);
                return 1;
            }
            else {
                return 3;
            }

        }
        else
        {
            return 2;
        }
    }

    public int saveWorkflow(long id, BPMNfile bpmn, long whoEdits){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(process_.getCanEdit().contains(whoEdits_)) {
                bpmnParser.saveBPMN(bpmn, process_);
                return 1;
            }
            return 3;
        }
        else
        {
            return 2;
        }
    }

    public int restoreWorkflow(long id, HistoryBPMN bpmn, long whoEdits){
        if(processRepository.existsById(id)){
            Process process_ = processRepository.findById(id).get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(process_.getCanEdit().contains(whoEdits_)) {
                if (bpmnParser.canRestoreBPMN(bpmn.getBpmnContent())) {
                    BPMNfile file = new BPMNfile();
                    file.setProcess(process_);
                    file.setBpmnContent(bpmn.getBpmnContent());
                    bpmnParser.saveBPMN(file, process_);
                    return 1;
                } else {
                    return 4;
                }
            }
            return 3;
        } else {
            return 2;
        }
    }

    public int newProcessInProject(Process newProcess, Process template, Project project){
        if(!(template == null)){
            newProcess = fillProcess(newProcess, template);
            newProcess = processRepository.save(newProcess);
            for(Element e : template.getElements()){
                var list = e.getPartOfProcess();
                list.add(newProcess);
                e.setPartOfProcess(list);
                elementRepository.save(e);
            }
            BPMNfile templateBPMN = template.getWorkflow();
            if(!(templateBPMN == null))
            {
                BPMNfile newBPMN = new BPMNfile();
                newBPMN.setBpmnContent(templateBPMN.getBpmnContent());
                newBPMN.setProcess(newProcess);
                bpmNfileRepository.save(newBPMN);
            }

        }
        newProcess.setProject(project);
        processRepository.save(newProcess);
        return 1;
    }

    public List<Process> getAllTemplates(long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            List<Process> allTemplates = processRepository.findAllTemplatesProcessesForUser(user);
             return allTemplates;
        }else return null;
    }

    public boolean addProcessFromFile(ProcessAndBpmnHolder holder, long whoEdits){
        Process newProcess = holder.getProcess();
        newProcess = processRepository.save(newProcess);
        BPMNfile newWorkflow = holder.getBpmn();
        newWorkflow.setBpmnContent(bpmnParser.prepareImportedFile(newWorkflow.getBpmnContent()));

        this.saveWorkflow(newProcess.getId(), newWorkflow, whoEdits);
        return true;
    }

    public int addMetric(Long id, ProcessMetric metric, long whoEdits) {
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(process_.getCanEdit().contains(whoEdits_)) {

                metric.setProcess(process_);
                processMetricRepository.save(metric);

                return 1;
            }
            return 3;
        }
        else
        {
            return 2;
        }
    }

    public int removeMetric(Long id, ProcessMetric metric, long whoEdits) {
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(process_.getCanEdit().contains(whoEdits_)) {
                ProcessMetric metric_ = processMetricRepository.findById(metric.getId()).get();
                if (metric_.getProcess().getId() == process_.getId()) {
                    processMetricRepository.delete(metric_);
                    return 1;
                } else {
                    return 4;
                }
            }
            return 3;

        }
        else
        {
            return 2;
        }
    }
}
