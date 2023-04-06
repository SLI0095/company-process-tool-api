package cz.sli0095.promod.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;

@Entity
public class BPMNfile {

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Basic.class)
    @Lob
    private String bpmnContent;

    @JsonIgnore
    @OneToOne(mappedBy = "workflow")
    private Process process;

    public BPMNfile() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBpmnContent() {
        return bpmnContent;
    }

    public void setBpmnContent(String bpmnContent) {
        this.bpmnContent = bpmnContent;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
