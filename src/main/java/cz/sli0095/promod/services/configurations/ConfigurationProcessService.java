package cz.sli0095.promod.services.configurations;

import cz.sli0095.promod.entities.*;
import cz.sli0095.promod.entities.Process;
import cz.sli0095.promod.repositories.BPMNfileRepository;
import cz.sli0095.promod.repositories.ProcessMetricRepository;
import cz.sli0095.promod.repositories.ProcessRepository;
import cz.sli0095.promod.repositories.TaskRepository;
import cz.sli0095.promod.services.BPMNparser;
import cz.sli0095.promod.services.ProcessService;
import cz.sli0095.promod.utils.BPMNSnapshotUtil;
import cz.sli0095.promod.utils.CompanyProcessToolConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ConfigurationProcessService {

    @Autowired
    ProcessRepository processRepository;
    @Autowired
    ProcessMetricRepository processMetricRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    BPMNfileRepository bpmnFileRepository;
    @Autowired
    ConfigurationTaskService configurationTaskService;
    @Autowired
    BPMNparser bpmNparser;

    @Autowired
    ProcessService processService;

    public Process createNewConfiguration(Process defaultProcess, ConfigurationHelper helper, BPMNSnapshotUtil snapshotWorkflow, User user, Project project){
        if(helper == null){
            helper = new ConfigurationHelper();
        }
        Process process = new Process();
        process.setName(defaultProcess.getName());
        process.setBriefDescription(defaultProcess.getBriefDescription());
        process.setMainDescription(defaultProcess.getMainDescription());
        process.setVersion(defaultProcess.getVersion());
        process.setChangeDate(defaultProcess.getChangeDate());
        process.setChangeDescription(defaultProcess.getChangeDescription());
        process.setPurpose(defaultProcess.getPurpose());
        process.setScope(defaultProcess.getScope());
        process.setAlternatives(defaultProcess.getAlternatives());
        process.setUsageNotes(defaultProcess.getUsageNotes());
        process.setHowToStaff(defaultProcess.getHowToStaff());
        process.setKeyConsiderations(defaultProcess.getKeyConsiderations());
        process.setTemplate(true);

        process.setCreatedFrom(defaultProcess);
        process.setProject(project);
        if(project == null){
            process.setOwner(user);
        } else {
            process.setOwner(project.getProjectOwner());
        }
        process = processRepository.save(process);

        BPMNSnapshotUtil snapshotBPMN = new BPMNSnapshotUtil(null);
        if(defaultProcess.getWorkflow() != null){
            snapshotBPMN.changeTo(defaultProcess.getWorkflow().getBpmnContent());
        }

        for(ProcessMetric defaultMetric : defaultProcess.getMetrics()){
            ProcessMetric metric = new ProcessMetric();
            metric.setName(defaultMetric.getName());
            metric.setDescription(defaultMetric.getDescription());
            metric.setProcess(process);
            processMetricRepository.save(metric);
        }

        var order = new ArrayList<>(defaultProcess.getElementsOrder());
        for(Element defaultElement : defaultProcess.getElements()){
            if(defaultElement instanceof Task){
                Task task = (Task) helper.getExistingElement(defaultElement.getId());
                if(task == null){
                    task = configurationTaskService.createNewConfiguration((Task) defaultElement, helper, snapshotBPMN, user, project);
                }
                var partOf = task.getPartOfProcess();
                if(!partOf.contains(process)){
                    partOf.add(process);
                    task.setPartOfProcess(partOf);
                    taskRepository.save(task);
                }
                int i = order.indexOf(defaultElement.getId());
                order.set(i, task.getId());
            } else {
                Process subProcess = (Process) helper.getExistingElement(defaultElement.getId());
                if(subProcess == null){
                    subProcess = this.createNewConfiguration((Process) defaultElement, helper, snapshotBPMN, user, project);
                }
                var partOf = subProcess.getPartOfProcess();
                if(!partOf.contains(process)){
                    partOf.add(process);
                    subProcess.setPartOfProcess(partOf);
                    subProcess = processRepository.save(subProcess);

                    //Change old id in workflow
                    if(snapshotBPMN.toString() != null){
                        String content = snapshotBPMN.toString();
                        String originalId = CompanyProcessToolConst.ELEMENT_ + defaultElement.getId() + "_";
                        String newId = CompanyProcessToolConst.ELEMENT_ + subProcess.getId() + "_";
                        content = bpmNparser.replaceIdInSnapshotWorkflow(content, originalId, newId);
                        snapshotBPMN.changeTo(content);
                    }
                }
                int i = order.indexOf(defaultElement.getId());
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
        helper.addElement(defaultProcess.getId(), process);
        return process;
    }
}
