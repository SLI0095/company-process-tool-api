package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotElement;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotProcess;
import com.semestral_project.company_process_tool.repositories.*;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotProcessService;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotsHelper;
import com.semestral_project.company_process_tool.utils.ItemUsersUtil;
import com.semestral_project.company_process_tool.utils.ProcessAndBpmnHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipOutputStream;

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
    UserRepository userRepository;
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

        return processData.orElse(null);
    }

    public List<Process> getUsableIn(long id){
        Process process = getProcessById(id);
        if(process == null){
            return null;
        }
        return process.getCanBeUsedIn();
    }

    public long addProcess(Process process, long userId){
        User owner = userService.getUserById(userId);
        if(owner == null){
            return -1;
        }
        process.setOwner(owner);
        process = processRepository.save(process);
        return process.getId();

//        try {
//            if(userRepository.existsById(userId)) {
//                User user = userRepository.findById(userId).get();
//                var list = process.getCanEdit();
//                list.add(user);
//                process = processRepository.save(process);
//                return process.getId();
//            }
//            else return -1;
//        }
//        catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//            return -1;
//        }
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

//        Optional<Process> processData = processRepository.findById(processId);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(process_.getCanEdit().contains(whoEdits_)){
//                User getAccess_ = userRepository.findById(getAccess.getId()).get();
//                if(process_.getHasAccess().contains(getAccess_)) {
//                    return 3; //already has access
//                }
//                if(process_.getCanEdit().contains(getAccess_)){
//                    var list = process_.getCanEdit();
//                    if(list.size() == 1){
//                        return 6;
//                    }
//                    list.remove(getAccess_);
//                    process_.setCanEdit(list);
//                }
//                var list = process_.getHasAccess();
//                list.add(getAccess_);
//                process_.setHasAccess(list);
//                processRepository.save(process_);
//                for(Element e : process_.getElements())
//                {
//                    if(e.getClass() == Task.class){
//                        taskService.addAccess(e.getId(), whoEdits_.getId(),getAccess);
//                    } else {
//                        this.addAccess(e.getId(),whoEdits_.getId(), getAccess); //
//                    }
//                }
//                return 1; //OK
//
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
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

//        Optional<Process> processData = processRepository.findById(processId);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(process_.getCanEdit().contains(whoEdits_)){
//                User getAccess_ = userRepository.findById(removeAccess.getId()).get();
//                if(process_.getHasAccess().contains(getAccess_)) {
//                    var list = process_.getHasAccess();
//                    list.remove(getAccess_);
//                    process_.setHasAccess(list);
//                    processRepository.save(process_);
//                    return 1; //access removed
//                } else{
//                    return 3; //nothing to remove
//                }
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
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

//        Optional<Process> processData = processRepository.findById(processId);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(process_.getCanEdit().contains(whoEdits_)){
//                User removeEdit_ = userRepository.findById(removeEdit.getId()).get();
//                if(process_.getCanEdit().contains(removeEdit_)) {
//                    var list = process_.getCanEdit();
//                    if(list.size() == 1){
//                        return 6;
//                    }
//                    list.remove(removeEdit_);
//                    process_.setCanEdit(list);
//                    processRepository.save(process_);
//                    return 1; //edit removed
//                } else{
//                    return 3; //nothing to remove
//                }
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
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


//        Optional<Process> processData = processRepository.findById(processId);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(process_.getCanEdit().contains(whoEdits_)){
//                User getEdit_ = userRepository.findById(getEdit.getId()).get();
//                if(process_.getCanEdit().contains(getEdit_)){
//                    return 4; //already can edit
//                } if(process_.getHasAccess().contains(getEdit_)) {
//                    var list = process_.getHasAccess();
//                    list.remove(getEdit_);
//                    process_.setHasAccess(list);
//                }
//                var list = process_.getCanEdit();
//                list.add(getEdit_);
//                process_.setCanEdit(list);
//                processRepository.save(process_);
//                for(Element e : process_.getElements())
//                {
//                    if(e.getClass() == Task.class){
//                        taskService.addEdit(e.getId(), whoEdits_.getId(),getEdit);
//                    } else {
//                        this.addEdit(e.getId(),whoEdits_.getId(), getEdit); //
//                    }
//                }
//                    return 1; //OK
//
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
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


//        if(!(getEdit instanceof User)){
//            return 1;
//        }
//        Optional<Process> processData = processRepository.findById(processId);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            User getEdit_ = userRepository.findById(getEdit.getId()).get();
//            if (process_.getCanEdit().contains(getEdit_)) {
//                return 4; //already can edit
//            }
//            if (process_.getHasAccess().contains(getEdit_)) {
//                var list = process_.getHasAccess();
//                list.remove(getEdit_);
//                process_.setHasAccess(list);
//            }
//            var list = process_.getCanEdit();
//            list.add(getEdit_);
//            process_.setCanEdit(list);
//            processRepository.save(process_);
//            for (Element e : process_.getElements()) {
//                if (e.getClass() == Task.class) {
//                    taskService.addEditAutomatic(e.getId(), getEdit);
//                } else {
//                    this.addEditAutomatic(e.getId(), getEdit);
//                }
//            }
//            return 1; //OK
//        }
//        else
//        {
//            return 2; //role not found
//        }
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

//        if(!(getAccess instanceof User)){
//            return 1;
//        }
//        Optional<Process> processData = processRepository.findById(processId);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//
//            User getAccess_ = userRepository.findById(getAccess.getId()).get();
//            if (process_.getHasAccess().contains(getAccess_)) {
//                return 3; //already has access
//            }
//            if (process_.getCanEdit().contains(getAccess_)) {
//                var list = process_.getCanEdit();
//                list.remove(getAccess_);
//                process_.setCanEdit(list);
//            }
//            var list = process_.getHasAccess();
//            list.add(getAccess_);
//            process_.setHasAccess(list);
//            processRepository.save(process_);
//            for (Element e : process_.getElements()) {
//                if (e.getClass() == Task.class) {
//                    taskService.addAccessAutomatic(e.getId(), getAccess);
//                } else {
//                    this.addAccessAutomatic(e.getId(), getAccess); //
//                }
//            }
//            return 1; //OK
//        }
//        else
//        {
//            return 2; //role not found
//        }
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
        var elements = process.getElements();
        for(Element e : elements){
            var list = e.getPartOfProcess();
            list.remove(process);
            e.setPartOfProcess(list);
            elementRepository.save(e);
        }
        for(SnapshotElement snapshot : process.getSnapshots()){
            snapshot.setOriginalElement(null);
        }
        processRepository.delete(process);
        return 1;

//        Optional<Process> processData = processRepository.findById(id);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            if (bpmnParser.removeProcessFromAllWorkflows(process_)) {
//                var elements = process_.getElements();
//                for(Element e : elements){
//                    var list = e.getPartOfProcess();
//                    list.remove(process_);
//                    e.setPartOfProcess(list);
//                    elementRepository.save(e);
//                }
//                for(SnapshotElement snapshot : process_.getSnapshots()){
//                    snapshot.setOriginalElement(null);
//                }
//                processRepository.deleteById(id);
//                return 1;
//            }
//            return 3;
//        }
//        return 2;
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
        bpmnParser.updateProcessInAllWorkflows(process, true, null);
        return 1;

//        Optional<Process> processData = processRepository.findById(id);
//        if(processData.isPresent()){
//            Process process_ = processData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(process_.getCanEdit().contains(whoEdits_)) {
//                process_ = fillProcess(process_, process);
//                processRepository.save(process_);
//                bpmnParser.updateProcessInAllWorkflows(process_, true, null);
//                return 1;
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
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
        orderList.add(element.getId());
        process.setElementsOrder(orderList);

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

//        Optional<Process> processData = processRepository.findById(id);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            Element element_ = elementRepository.findById(element.getId()).get();
//            var isPartOf = element_.getPartOfProcess();
//            if(isPartOf.contains(process_))
//            {
//                return 3;
//            }
//            isPartOf.add(process_);
//            element_.setPartOfProcess(isPartOf);
//            elementRepository.save(element_);
//            //add access and edit from process to element
//            for(UserType u : process_.getCanEdit()){
//                if(element_.getClass() == Task.class){
//                    taskService.addEditAutomatic(element_.getId(), u);
//                } else {
//                    this.addEditAutomatic(element_.getId(), u);
//                }
//            }
//            for(UserType u : process_.getHasAccess()){
//                if(element_.getClass() == Task.class){
//                    taskService.addAccessAutomatic(element_.getId(), u);
//                } else {
//                    this.addAccessAutomatic(element_.getId(), u);
//                }
//            }
//            return 1;
//        }
//        else
//        {
//            return 2;
//        }
    }

//    public int removeElementFromProcess(long id, Element element){
//        Optional<Process> processData = processRepository.findById(id);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            Element element_ = elementRepository.findById(element.getId()).get();
//            var elementList = process_.getElements();
//            if(elementList.contains(element_)) {
//                elementList.remove(element_);
//                process_.setElements(elementList);
//                processRepository.save(process_);
//                return 1;
//            }
//            else {
//                return 3;
//            }
//
//        }
//        else
//        {
//            return 2;
//        }
//    }

    public int saveWorkflow(long id, BPMNfile bpmn, long whoEdits){
        Process process = getProcessById(id);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 5; //cannot edit
        }
        bpmnParser.saveBPMN(bpmn, process, editor);
        return 1;

//        Optional<Process> processData = processRepository.findById(id);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(process_.getCanEdit().contains(whoEdits_)) {
//                bpmnParser.saveBPMN(bpmn, process_);
//                return 1;
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
    }

    public int restoreWorkflow(long id, HistoryBPMN bpmn, long whoEdits){
        Process process = getProcessById(id);
        if(process == null){
            return 2; //process not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 3; //cannot edit
        }
        if (!bpmnParser.canRestoreBPMN(bpmn.getBpmnContent())) {
            return 3;
        }

        BPMNfile file = new BPMNfile();
        file.setProcess(process);
        file.setBpmnContent(bpmn.getBpmnContent());
        bpmnParser.saveBPMN(file, process, editor);
        return 1;

//        if(processRepository.existsById(id)){
//            Process process_ = processRepository.findById(id).get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(process_.getCanEdit().contains(whoEdits_)) {
//                if (bpmnParser.canRestoreBPMN(bpmn.getBpmnContent())) {
//                    BPMNfile file = new BPMNfile();
//                    file.setProcess(process_);
//                    file.setBpmnContent(bpmn.getBpmnContent());
//                    bpmnParser.saveBPMN(file, process_);
//                    return 1;
//                } else {
//                    return 4;
//                }
//            }
//            return 3;
//        } else {
//            return 2;
//        }
    }


    @Deprecated
    public long newVersionOfProcess(Process newProcess, long oldProcess, long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            var list = newProcess.getCanEdit();
            list.add(user);
            newProcess.setCanEdit(list);
            newProcess = processRepository.save(newProcess);

            Process old = processRepository.findById(oldProcess).get();
            if(old.getWorkflow() != null){
                BPMNfile newFile = new BPMNfile();
                newFile.setBpmnContent(old.getWorkflow().getBpmnContent());
                bpmnParser.saveBPMN(newFile, newProcess, user);
            }
            return newProcess.getId();
        }else return -1;
    }

    public List<Process> getAllUserCanView(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Process> ret = new HashSet<>();
        List<Process> processes = (List<Process>) processRepository.findAll();
        for(Process p : processes){
            if(ItemUsersUtil.getAllUsersCanView(p).contains(user)){
                ret.add(p);
            }
        }
        return new ArrayList<>(ret);
    }

    public List<Process> getAllUserCanEdit(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Process> ret = new HashSet<>();
        List<Process> processes = (List<Process>) processRepository.findAll();
        for(Process p : processes){
            if(ItemUsersUtil.getAllUsersCanEdit(p).contains(user)){
                ret.add(p);
            }
        }
        return new ArrayList<>(ret);
    }

    public List<Process> getAllUserCanViewByTemplate(long userId, boolean isTemplate){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Process> ret = new HashSet<>();
        List<Process> processes = processRepository.findByIsTemplate(isTemplate);
        for(Process p : processes){
            if(ItemUsersUtil.getAllUsersCanView(p).contains(user)){
                ret.add(p);
            }
        }
        return new ArrayList<>(ret);
    }

    public List<Process> getUsableInProcessForUser(long userId, Process process){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Process> ret = new HashSet<>();
        List<Process> processes = processRepository.usableInProcessForUser(process);
        for(Process p : processes){
            if(ItemUsersUtil.getAllUsersCanView(p).contains(user)){
                ret.add(p);
            }
        }
        return new ArrayList<>(ret);
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

//        Optional<Process> processData = processRepository.findById(id);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(process_.getCanEdit().contains(whoEdits_)) {
//
//                metric.setProcess(process_);
//                processMetricRepository.save(metric);
//
//                return 1;
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
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

//        Optional<Process> processData = processRepository.findById(id);
//        if(processData.isPresent()) {
//            Process process_ = processData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(process_.getCanEdit().contains(whoEdits_)) {
//                ProcessMetric metric_ = processMetricRepository.findById(metric.getId()).get();
//                if (metric_.getProcess().getId() == process_.getId()) {
//                    processMetricRepository.delete(metric_);
//                    return 1;
//                } else {
//                    return 4;
//                }
//            }
//            return 3;
//
//        }
//        else
//        {
//            return 2;
//        }
    }

    public ZipOutputStream generateHTML(long id, OutputStream stream){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            return htmlGenerator.generateHTML(id, stream);
        }
        return null;
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

    public int createSnapshot(Long id, long userId, String description) {
        //TODO test method for testing user groups
        Process process = getProcessById(id);
        if(process == null){
            return 2;
        }
        User editor = userService.getUserById(userId);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(process).contains(editor)){
            return 3;
        }
        snapshotProcessService.createSnapshot(process, description, new SnapshotsHelper());
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
}
