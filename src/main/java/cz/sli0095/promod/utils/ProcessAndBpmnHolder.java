package cz.sli0095.promod.utils;

import cz.sli0095.promod.entities.BPMNfile;
import cz.sli0095.promod.entities.Process;

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
