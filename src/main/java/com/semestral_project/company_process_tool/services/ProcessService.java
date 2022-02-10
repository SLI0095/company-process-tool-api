package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.BPMNfileRepository;
import com.semestral_project.company_process_tool.repositories.ElementRepository;
import com.semestral_project.company_process_tool.repositories.ProcessRepository;
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

    private Process fillProcess(Process oldProcess, Process updatedProcess){
        oldProcess.setName(updatedProcess.getName());
        oldProcess.setBriefDescription(updatedProcess.getBriefDescription());
        oldProcess.setMainDescription(updatedProcess.getMainDescription());
        oldProcess.setVersion(updatedProcess.getVersion());
        oldProcess.setChangeDate(updatedProcess.getChangeDate());
        oldProcess.setChangeDescription(updatedProcess.getChangeDescription());
        oldProcess.setPurpose(updatedProcess.getPurpose());
        oldProcess.setScope(updatedProcess.getScope());
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

    public boolean addProcess(Process process){
        try {
            processRepository.save(process);
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteProcessById(long id){
        try {
            if(bpmnParser.removeProcessFromAllWorkflows(processRepository.findById(id).get()))
            {
                processRepository.deleteById(id);
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int updateProcess(long id, Process process){
        Optional<Process> processData = processRepository.findById(id);

        if(processData.isPresent()){
            Process process_ = processData.get();
            process_ = fillProcess(process_, process);
            processRepository.save(process_);
            bpmnParser.updateProcessInAllWorkflows(process_, true, null);
            return 1;
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
            var elementList = process_.getElements();
            if(elementList.contains(element_))
            {
                return 3;
            }
            elementList.add(element_);
            process_.setElements(elementList);

            processRepository.save(process_);
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

    public int saveWorkflow(long id, BPMNfile bpmn){
        Optional<Process> processData = processRepository.findById(id);
        if(processData.isPresent()) {
            Process process_ = processData.get();
            bpmnParser.saveBPMN(bpmn, process_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public int restoreWorkflow(long id, HistoryBPMN bpmn){
        if(processRepository.existsById(id)){
            if(bpmnParser.canRestoreBPMN(bpmn.getBpmnContent())){
                BPMNfile file = new BPMNfile();
                Process process_ = processRepository.findById(id).get();
                file.setProcess(process_);
                file.setBpmnContent(bpmn.getBpmnContent());
                bpmnParser.saveBPMN(file, process_);
                return 1;
            } else {
                return 3;
            }
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

    public List<Process> getAllTemplates(){
        List<Process> allTemplates = processRepository.findAllTemplates();
        return allTemplates;
    }
}
