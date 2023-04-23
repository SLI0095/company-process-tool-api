package cz.sli0095.promod.services.snaphsots;

import cz.sli0095.promod.entities.*;
import cz.sli0095.promod.entities.Process;
import cz.sli0095.promod.entities.snapshots.*;
import cz.sli0095.promod.repositories.*;
import cz.sli0095.promod.repositories.snapshots.SnapshotBPMNRepository;
import cz.sli0095.promod.repositories.snapshots.SnapshotProcessMetricRepository;
import cz.sli0095.promod.repositories.snapshots.SnapshotProcessRepository;
import cz.sli0095.promod.repositories.snapshots.SnapshotTaskRepository;
import cz.sli0095.promod.services.BPMNparser;
import cz.sli0095.promod.services.ProcessService;
import cz.sli0095.promod.utils.BPMNSnapshotUtil;
import cz.sli0095.promod.utils.CompanyProcessToolConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public SnapshotProcess createSnapshot(Process original, SnapshotItem snapshotDetail, SnapshotsHelper helper){
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

        snapshot.setSnapshotName(snapshotDetail.getSnapshotName());
        snapshot.setSnapshotDescription(snapshotDetail.getSnapshotDescription());
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
        var order = new ArrayList<>(original.getElementsOrder());
        for(Element element : original.getElements()){
            if(element instanceof Task){
                SnapshotTask snapshotTask = (SnapshotTask) helper.getExistingSnapshotElement(element.getId());
                if(snapshotTask == null){
                    snapshotTask = snapshotTaskService.createSnapshot((Task)element, snapshotDetail, helper);
                }
                var partOf = snapshotTask.getPartOfProcess();
                if(!partOf.contains(snapshot)){
                    partOf.add(snapshot);
                    snapshotTask.setPartOfProcess(partOf);
                    snapshotTaskRepository.save(snapshotTask);
                }
                int i = order.indexOf(snapshotTask.getOriginalId());
                order.set(i, snapshotTask.getId());
            } else {
                SnapshotProcess snapshotProcess = (SnapshotProcess) helper.getExistingSnapshotElement(element.getId());
                if(snapshotProcess == null){
                    snapshotProcess = this.createSnapshot((Process)element, snapshotDetail, helper);
                }
                var partOf = snapshotProcess.getPartOfProcess();
                if(!partOf.contains(snapshot)){
                    partOf.add(snapshot);
                    snapshotProcess.setPartOfProcess(partOf);
                    snapshotProcessRepository.save(snapshotProcess);
                }
                int i = order.indexOf(snapshotProcess.getOriginalId());
                order.set(i, snapshotProcess.getId());
            }
        }
        snapshot.setElementsOrder(order);
        snapshot = snapshotProcessRepository.save(snapshot);
        helper.addElement(original.getId(), snapshot);
        return snapshot;
    }

    public Process restoreFromSnapshot(SnapshotProcess snapshotProcess, SnapshotsHelper helper, BPMNSnapshotUtil snapshotWorkflow, User user){
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

        process.setOwner(user);

        process = processRepository.save(process);

        BPMNSnapshotUtil snapshotBPMN = new BPMNSnapshotUtil(null);
        if(snapshotProcess.getWorkflow() != null){
            snapshotBPMN.changeTo(snapshotProcess.getWorkflow().getBpmnContent());
        }

        for(SnapshotProcessMetric snapMetric : snapshotProcess.getMetrics()){
            ProcessMetric metric = new ProcessMetric();
            metric.setName(snapMetric.getName());
            metric.setDescription(snapMetric.getDescription());
            metric.setProcess(process);
            processMetricRepository.save(metric);
        }

        var order = new ArrayList<>(snapshotProcess.getElementsOrder());
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
                int i = order.indexOf(snapshotElement.getId());
                order.set(i, task.getId());
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
                    if(snapshotBPMN.toString() != null){
                        String content = snapshotWorkflow.toString();
                        String originalId = CompanyProcessToolConst.ELEMENT_ + snapshotElement.getOriginalId().toString() + "_";
                        String newId = CompanyProcessToolConst.ELEMENT_ + subProcess.getId() + "_";
                        content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                        snapshotBPMN.changeTo(content);
                    }
                }
                int i = order.indexOf(snapshotElement.getId());
                order.set(i, subProcess.getId());
            }
        }
        if(snapshotBPMN.toString() != null) {
            BPMNfile workflow = new BPMNfile();
            workflow.setBpmnContent(snapshotBPMN.toString());
            workflow.setProcess(process);
            workflow = bpmnFileRepository.save(workflow);
            process.setWorkflow(workflow);
        }
        process.setElementsOrder(order);
        process = processRepository.save(process);
        helper.addElement(snapshotProcess.getId(), process);
        return process;
    }

    public SnapshotProcess getSnapshotProcessById(long id) {
        Optional<SnapshotProcess> processData = snapshotProcessRepository.findById(id);
        return processData.orElse(null);
    }

    public Process revertFromSnapshot(SnapshotProcess snapshotProcess, SnapshotsHelper helper, BPMNSnapshotUtil snapshotWorkflow, User user){
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

            process.setOwner(user);

        }
        process = processRepository.save(process);

        BPMNSnapshotUtil snapshotBPMN = new BPMNSnapshotUtil(null);
        if(snapshotProcess.getWorkflow() != null){
            snapshotBPMN.changeTo(snapshotProcess.getWorkflow().getBpmnContent());
        }

        var order = new ArrayList<>(snapshotProcess.getElementsOrder());
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
                int i = order.indexOf(snapshotElement.getId());
                order.set(i, task.getId());
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

                    if(subProcess.getId() != snapshotElement.getOriginalId() && snapshotBPMN.toString() != null){
                        //Change old id in workflow
                        String content = snapshotWorkflow.toString();
                        String originalId = CompanyProcessToolConst.ELEMENT_ + snapshotElement.getOriginalId().toString() + "_";
                        String newId = CompanyProcessToolConst.ELEMENT_ + subProcess.getId() + "_";
                        content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                        snapshotBPMN.changeTo(content);
                    }
                }
                int i = order.indexOf(snapshotElement.getId());
                order.set(i, subProcess.getId());
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
        if(snapshotBPMN.toString() != null){
            BPMNfile workflow = new BPMNfile();
            workflow.setBpmnContent(snapshotBPMN.toString());
            workflow.setProcess(process);
            workflow = bpmnFileRepository.save(workflow);
            process.setWorkflow(workflow);
        }
        process.setElementsOrder(order);
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
