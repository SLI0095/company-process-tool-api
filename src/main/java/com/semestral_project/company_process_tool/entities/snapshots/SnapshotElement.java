package com.semestral_project.company_process_tool.entities.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.Task;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="snapshot_element_type",
        discriminatorType = DiscriminatorType.STRING)
public class SnapshotElement extends SnapshotItem{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Element originalElement;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "snapshot_element_snapshot_process",
            joinColumns = {@JoinColumn(name = "snapshot_element_id")},
            inverseJoinColumns = {@JoinColumn(name = "snapshot_process_id")})
    private List<SnapshotProcess> partOfProcess = new ArrayList<>();


}
