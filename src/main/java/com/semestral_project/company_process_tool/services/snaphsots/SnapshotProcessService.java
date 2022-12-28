package com.semestral_project.company_process_tool.services.snaphsots;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.snapshots.*;
import com.semestral_project.company_process_tool.repositories.*;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotBPMNRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotProcessMetricRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotProcessRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotTaskRepository;
import com.semestral_project.company_process_tool.services.BPMNparser;
import com.semestral_project.company_process_tool.services.ProcessService;
import com.semestral_project.company_process_tool.utils.CompanyProcessToolConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SnapshotProcessService {

    @Autowired
    SnapshotTaskService snapshotTaskService;

    @Autowired
    SnapshotProcessMetricRepository snapshotProcessMetricRepository;

    @Autowired
    SnapshotProcessRepository snapshotProcessRepository;

    @Autowired
    SnapshotBPMNRepository snapshotBPMNRepository;

    @Autowired
    SnapshotTaskRepository snapshotTaskRepository;
    @Autowired
    ProcessRepository processRepository;
    @Autowired
    ProcessMetricRepository processMetricRepository;
    @Autowired
    BPMNfileRepository bpmnFileRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    BPMNparser bpmNparser;
    @Autowired
    ProcessService processService;
    @Autowired
    ElementRepository elementRepository;

    @Transactional
    public SnapshotProcess createSnapshot(Process original, String snapshotDescription, SnapshotsHelper helper){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        SnapshotProcess snapshot = new SnapshotProcess();
        snapshot.setName(original.getName());
        snapshot.setBriefDescription(original.getBriefDescription());
        snapshot.setMainDescription(original.getMainDescription());
        snapshot.setVersion(original.getVersion());
        snapshot.setChangeDate(original.getChangeDate());
        snapshot.setChangeDescription(original.getChangeDescription());
        snapshot.setPurpose(original.getPurpose());
        snapshot.setScope(original.getScope());
        snapshot.setAlternatives(original.getAlternatives());
        snapshot.setUsageNotes(original.getUsageNotes());
        snapshot.setHowToStaff(original.getHowToStaff());
        snapshot.setKeyConsiderations(original.getKeyConsiderations());

        snapshot.setSnapshotDescription(snapshotDescription);
        snapshot.setSnapshotDate(LocalDate.now());
        snapshot.setOriginalElement(original);
        snapshot.setOriginalId(original.getId());

        snapshot = snapshotProcessRepository.save(snapshot);


        if(original.getWorkflow() != null){
            SnapshotBPMN workflow = new SnapshotBPMN();
            workflow.setBpmnContent(original.getWorkflow().getBpmnContent());
            workflow.setProcess(snapshot);
            snapshotBPMNRepository.save(workflow);
            snapshot.setWorkflow(workflow);
        }

        for(ProcessMetric metric : original.getMetrics()){
            SnapshotProcessMetric snapshotMetric = new SnapshotProcessMetric();
            snapshotMetric.setName(metric.getName());
            snapshotMetric.setDescription(metric.getDescription());
            snapshotMetric.setProcess(snapshot);
            snapshotProcessMetricRepository.save(snapshotMetric);
        }

        for(Element element : original.getElements()){
            if(element instanceof Task){
                SnapshotTask snapshotTask = (SnapshotTask) helper.getExistingSnapshotElement(element.getId());
                if(snapshotTask == null){
                    snapshotTask = snapshotTaskService.createSnapshot((Task)element, snapshotDescription, helper);
                }
                var partOf = snapshotTask.getPartOfProcess();
                if(!partOf.contains(snapshot)){
                    partOf.add(snapshot);
                    snapshotTask.setPartOfProcess(partOf);
                    snapshotTaskRepository.save(snapshotTask);
                }
            } else {
                SnapshotProcess snapshotProcess = (SnapshotProcess) helper.getExistingSnapshotElement(element.getId());
                if(snapshotProcess == null){
                    snapshotProcess = this.createSnapshot((Process)element, snapshotDescription, helper);
                }
                var partOf = snapshotProcess.getPartOfProcess();
                if(!partOf.contains(snapshot)){
                    partOf.add(snapshot);
                    snapshotProcess.setPartOfProcess(partOf);
                    snapshotProcessRepository.save(snapshotProcess);
                }
            }
        }

        snapshot = snapshotProcessRepository.save(snapshot);
        helper.addElement(original.getId(), snapshot);
        return snapshot;
    }

    @Transactional
    public Process restoreFromSnapshot(SnapshotProcess snapshotProcess, SnapshotsHelper helper, SnapshotBPMN snapshotWorkflow, User user){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        Process process = new Process();
        process.setName(snapshotProcess.getName());
        process.setBriefDescription(snapshotProcess.getBriefDescription());
        process.setMainDescription(snapshotProcess.getMainDescription());
        process.setVersion(snapshotProcess.getVersion());
        process.setChangeDate(snapshotProcess.getChangeDate());
        process.setChangeDescription(snapshotProcess.getChangeDescription());
        process.setPurpose(snapshotProcess.getPurpose());
        process.setScope(snapshotProcess.getScope());
        process.setAlternatives(snapshotProcess.getAlternatives());
        process.setUsageNotes(snapshotProcess.getUsageNotes());
        process.setHowToStaff(snapshotProcess.getHowToStaff());
        process.setKeyConsiderations(snapshotProcess.getKeyConsiderations());

        var list = process.getCanEdit();
        list.add(user);
        process.setCanEdit(list);
        process.setOwner(user);

        process = processRepository.save(process);

        SnapshotBPMN snapshotBPMN = snapshotProcess.getWorkflow();

        for(SnapshotProcessMetric snapMetric : snapshotProcess.getMetrics()){
            ProcessMetric metric = new ProcessMetric();
            metric.setName(snapMetric.getName());
            metric.setDescription(snapMetric.getDescription());
            metric.setProcess(process);
            processMetricRepository.save(metric);
        }

        for(SnapshotElement snapshotElement : snapshotProcess.getElements()){
            if(snapshotElement instanceof SnapshotTask){
                Task task = (Task) helper.getExistingElement(snapshotElement.getId());
                if(task == null){
                    task = snapshotTaskService.restoreFromSnapshot((SnapshotTask) snapshotElement, helper, snapshotBPMN, user);
                }
                var partOf = task.getPartOfProcess();
                if(!partOf.contains(process)){
                    partOf.add(process);
                    task.setPartOfProcess(partOf);
                    taskRepository.save(task);
                }
            } else {
                Process subProcess = (Process) helper.getExistingElement(snapshotElement.getId());
                if(subProcess == null){
                    subProcess = this.restoreFromSnapshot((SnapshotProcess) snapshotElement, helper, snapshotBPMN, user);
                }
                var partOf = subProcess.getPartOfProcess();
                if(!partOf.contains(process)){
                    partOf.add(process);
                    subProcess.setPartOfProcess(partOf);
                    subProcess = processRepository.save(subProcess);

                    //Change old id in workflow
                    if(snapshotBPMN != null){
                        String content = snapshotWorkflow.getBpmnContent();
                        String originalId = CompanyProcessToolConst.ELEMENT_ + snapshotElement.getOriginalId().toString() + "_";
                        String newId = CompanyProcessToolConst.ELEMENT_ + subProcess.getId() + "_";
                        content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                        snapshotBPMN.setBpmnContent(content);
                    }
                }
            }
        }
        if(snapshotBPMN != null) {
            BPMNfile workflow = new BPMNfile();
            workflow.setBpmnContent(snapshotBPMN.getBpmnContent());
            workflow.setProcess(process);
            workflow = bpmnFileRepository.save(workflow);
            process.setWorkflow(workflow);
        }
        process = processRepository.save(process);
        helper.addElement(snapshotProcess.getId(), process);
        return process;
    }

    public SnapshotProcess getSnapshotProcessById(long id) {
        Optional<SnapshotProcess> processData = snapshotProcessRepository.findById(id);
        return processData.orElse(null);
    }



    @Transactional
    //TODO fix unable to safe process - metric not found, check changes of id in workflow
    public Process revertFromSnapshot(SnapshotProcess snapshotProcess, SnapshotsHelper helper, SnapshotBPMN snapshotWorkflow, User user){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        Process process;
        boolean existing = existsProcess(snapshotProcess.getOriginalId());

        if(existing){
            process = processService.getProcessById(snapshotProcess.getOriginalId());

            process.setName(snapshotProcess.getName());
            process.setBriefDescription(snapshotProcess.getBriefDescription());
            process.setMainDescription(snapshotProcess.getMainDescription());
            process.setVersion(snapshotProcess.getVersion());
            process.setChangeDate(snapshotProcess.getChangeDate());
            process.setChangeDescription(snapshotProcess.getChangeDescription());
            process.setPurpose(snapshotProcess.getPurpose());
            process.setScope(snapshotProcess.getScope());
            process.setAlternatives(snapshotProcess.getAlternatives());
            process.setUsageNotes(snapshotProcess.getUsageNotes());
            process.setHowToStaff(snapshotProcess.getHowToStaff());
            process.setKeyConsiderations(snapshotProcess.getKeyConsiderations());

        } else {
            process = new Process();
            process.setName(snapshotProcess.getName());
            process.setBriefDescription(snapshotProcess.getBriefDescription());
            process.setMainDescription(snapshotProcess.getMainDescription());
            process.setVersion(snapshotProcess.getVersion());
            process.setChangeDate(snapshotProcess.getChangeDate());
            process.setChangeDescription(snapshotProcess.getChangeDescription());
            process.setPurpose(snapshotProcess.getPurpose());
            process.setScope(snapshotProcess.getScope());
            process.setAlternatives(snapshotProcess.getAlternatives());
            process.setUsageNotes(snapshotProcess.getUsageNotes());
            process.setHowToStaff(snapshotProcess.getHowToStaff());
            process.setKeyConsiderations(snapshotProcess.getKeyConsiderations());

            var list = process.getCanEdit();
            list.add(user);
            process.setCanEdit(list);
            process.setOwner(user);

        }
        process = processRepository.save(process);

        SnapshotBPMN snapshotBPMN = snapshotProcess.getWorkflow();

        List<Element> allElements = new ArrayList<>();
        for(SnapshotElement snapshotElement : snapshotProcess.getElements()){
            if(snapshotElement instanceof SnapshotTask){
                Task task = (Task) helper.getExistingElement(snapshotElement.getId());
                if(task == null){
                    if(snapshotTaskService.existsTask(snapshotElement.getOriginalId())){
                        task = snapshotTaskService.revertExistingFromSnapshot((SnapshotTask) snapshotElement, helper, snapshotBPMN, user);
                    } else {
                        task = snapshotTaskService.revertNonExistingFromSnapshot((SnapshotTask) snapshotElement, helper, snapshotBPMN, user);
                    }
                }
                allElements.add(task);
                var partOf = task.getPartOfProcess();
                if(!partOf.contains(process)){
                    partOf.add(process);
                    task.setPartOfProcess(partOf);
                    taskRepository.save(task);
                }
            } else {
                Process subProcess = (Process) helper.getExistingElement(snapshotElement.getId());
                if(subProcess == null){
                    subProcess = this.revertFromSnapshot((SnapshotProcess) snapshotElement, helper, snapshotBPMN, user);
                }
                allElements.add(subProcess);
                var partOf = subProcess.getPartOfProcess();
                if(!partOf.contains(process)){
                    partOf.add(process);
                    subProcess.setPartOfProcess(partOf);
                    subProcess = processRepository.save(subProcess);

                    if(subProcess.getId() != snapshotElement.getOriginalId() && snapshotBPMN != null){
                        //Change old id in workflow
                        String content = snapshotWorkflow.getBpmnContent();
                        String originalId = CompanyProcessToolConst.ELEMENT_ + snapshotElement.getOriginalId().toString() + "_";
                        String newId = CompanyProcessToolConst.ELEMENT_ + subProcess.getId() + "_";
                        content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                        snapshotBPMN.setBpmnContent(content);
                    }
                }
            }
        }
        for(Element e : process.getElements()){
            if(!allElements.contains(e)){
                var list = e.getPartOfProcess();
                list.remove(process);
                e.setPartOfProcess(list);
                elementRepository.save(e);
            }
        }

        process = processService.getProcessById(process.getId());

        if(existing){
            bpmNparser.updateProcessInAllWorkflows(process,true,null);
        }
        if(snapshotBPMN != null){
            BPMNfile workflow = new BPMNfile();
            workflow.setBpmnContent(snapshotBPMN.getBpmnContent());
            workflow.setProcess(process);
            workflow = bpmnFileRepository.save(workflow);
            process.setWorkflow(workflow);
        }
        process = processRepository.save(process);
        helper.addElement(snapshotProcess.getId(), process);

        if(existing){
            processService.deleteAllMetrics(process.getId());
        }
        for(SnapshotProcessMetric snapMetric : snapshotProcess.getMetrics()){
            ProcessMetric metric = new ProcessMetric();
            metric.setName(snapMetric.getName());
            metric.setDescription(snapMetric.getDescription());
            metric.setProcess(process);
            processMetricRepository.save(metric);
        }

        return process;
    }

    public boolean existsProcess(long id){
        return processRepository.existsById(id);
    }

}
