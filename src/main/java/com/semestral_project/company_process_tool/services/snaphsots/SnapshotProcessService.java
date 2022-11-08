package com.semestral_project.company_process_tool.services.snaphsots;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.ProcessMetric;
import com.semestral_project.company_process_tool.entities.Task;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotBPMN;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotProcess;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotProcessMetric;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotTask;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotBPMNRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotProcessMetricRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotProcessRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

        snapshot = snapshotProcessRepository.save(snapshot);

        SnapshotBPMN workflow = new SnapshotBPMN();
        workflow.setBpmnContent(original.getWorkflow().getBpmnContent());
        workflow.setProcess(snapshot);
        snapshotBPMNRepository.save(workflow);
        snapshot.setWorkflow(workflow);

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
}
