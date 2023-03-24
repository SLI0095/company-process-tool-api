package com.semestral_project.company_process_tool.utils;

import com.semestral_project.company_process_tool.entities.BPMNfile;
import com.semestral_project.company_process_tool.entities.Process;

public class ProcessAndBpmnHolder{

    private Process process;
    private BPMNfile bpmn;

    public ProcessAndBpmnHolder() {
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public BPMNfile getBpmn() {
        return bpmn;
    }

    public void setBpmn(BPMNfile bpmn) {
        this.bpmn = bpmn;
    }
}
