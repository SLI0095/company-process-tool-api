package cz.sli0095.promod.entities;

import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.LongListConverter;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("process")
public class Process extends Element{

    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String scope;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String usageNotes;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String alternatives;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String howToStaff;
    @JsonView(Views.Basic.class)
    @Column(columnDefinition="LONGTEXT")
    private String keyConsiderations;

    @JsonView(Views.Basic.class)
    @ManyToMany(mappedBy = "partOfProcess", cascade = CascadeType.DETACH)
    private List<Element> elements = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "bpmn_id")
    private BPMNfile workflow;

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE)
    private List<ProcessMetric> metrics = new ArrayList<>();

    @ManyToMany(mappedBy = "canBeUsedIn", cascade = CascadeType.DETACH)
    private List<Element> usableElements = new ArrayList<>();

    @ManyToMany(mappedBy = "canBeUsedInProcesses", cascade = CascadeType.DETACH)
    private List<WorkItem> usableWorkItems = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @Convert(converter = LongListConverter.class)
    private List<Long> elementsOrder = new ArrayList<>();

    public Process() {
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUsageNotes() {
        return usageNotes;
    }

    public void setUsageNotes(String usageNotes) {
        this.usageNotes = usageNotes;
    }

    public String getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(String alternatives) {
        this.alternatives = alternatives;
    }

    public String getHowToStaff() {
        return howToStaff;
    }

    public BPMNfile getWorkflow() {
        return workflow;
    }

    public void setWorkflow(BPMNfile workflow) {
        this.workflow = workflow;
    }

    public void setHowToStaff(String howToStaff) {
        this.howToStaff = howToStaff;
    }

    public String getKeyConsiderations() {
        return keyConsiderations;
    }

    public void setKeyConsiderations(String keyConsiderations) {
        this.keyConsiderations = keyConsiderations;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public List<ProcessMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<ProcessMetric> metrics) {
        this.metrics = metrics;
    }

    public List<Element> getUsableElements() {
        return usableElements;
    }

    public void setUsableElements(List<Element> usableElements) {
        this.usableElements = usableElements;
    }

    public List<WorkItem> getUsableWorkItems() {
        return usableWorkItems;
    }

    public void setUsableWorkItems(List<WorkItem> usableWorkItems) {
        this.usableWorkItems = usableWorkItems;
    }

    public List<Long> getElementsOrder() {
        return elementsOrder;
    }

    public void setElementsOrder(List<Long> elementsOrder) {
        this.elementsOrder = elementsOrder;
    }
}
