package cz.sli0095.promod.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.LongListConverter;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SnapshotProcess extends SnapshotElement {

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
    private List<SnapshotElement> elements = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "snapshot_bpmn_id")
    private SnapshotBPMN workflow;

    @JsonView(Views.Basic.class)
    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE)
    private List<SnapshotProcessMetric> metrics = new ArrayList<>();

    @JsonView(Views.Basic.class)
    @Convert(converter = LongListConverter.class)
    private List<Long> elementsOrder = new ArrayList<>();

    public SnapshotProcess() {
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

    public void setHowToStaff(String howToStaff) {
        this.howToStaff = howToStaff;
    }

    public String getKeyConsiderations() {
        return keyConsiderations;
    }

    public void setKeyConsiderations(String keyConsiderations) {
        this.keyConsiderations = keyConsiderations;
    }

    public List<SnapshotElement> getElements() {
        return elements;
    }

    public void setElements(List<SnapshotElement> elements) {
        this.elements = elements;
    }

    public SnapshotBPMN getWorkflow() {
        return workflow;
    }

    public void setWorkflow(SnapshotBPMN workflow) {
        this.workflow = workflow;
    }

    public List<SnapshotProcessMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<SnapshotProcessMetric> metrics) {
        this.metrics = metrics;
    }

    public List<Long> getElementsOrder() {
        return elementsOrder;
    }

    public void setElementsOrder(List<Long> elementsOrder) {
        this.elementsOrder = elementsOrder;
    }
}
