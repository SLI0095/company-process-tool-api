package cz.sli0095.promod.entities.snapshots;

import cz.sli0095.promod.entities.Element;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.utils.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="snapshot_element_type",
        discriminatorType = DiscriminatorType.STRING)
public class SnapshotElement extends SnapshotItem{

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    private Element originalElement;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "snapshot_element_snapshot_process",
            joinColumns = {@JoinColumn(name = "snapshot_element_id")},
            inverseJoinColumns = {@JoinColumn(name = "snapshot_process_id")})
    private List<SnapshotProcess> partOfProcess = new ArrayList<>();

    public SnapshotElement() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Element getOriginalElement() {
        return originalElement;
    }

    public void setOriginalElement(Element originalElement) {
        this.originalElement = originalElement;
    }

    public List<SnapshotProcess> getPartOfProcess() {
        return partOfProcess;
    }

    public void setPartOfProcess(List<SnapshotProcess> partOfProcess) {
        this.partOfProcess = partOfProcess;
    }
}
