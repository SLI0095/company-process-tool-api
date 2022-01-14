package com.semestral_project.company_process_tool.entities;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "work_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="work_item_type",
        discriminatorType = DiscriminatorType.STRING)
public class WorkItem extends Item{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    @JoinTable(name = "work_item_task_mandatory_input",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asMandatoryInput;

    @ManyToMany
    @JoinTable(name = "work_item_task_optional_input",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asOptionalInput;

    @ManyToMany
    @JoinTable(name = "work_item_task_output",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Task> asOutput;

    @ManyToMany
    @JoinTable(name = "work_item_activity_guidance",
            joinColumns = {@JoinColumn(name = "work_item_id")},
            inverseJoinColumns = {@JoinColumn(name = "element_id")})
    private List<Activity> asGuidanceWorkItem;

    public WorkItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Task> getAsMandatoryInput() {
        return asMandatoryInput;
    }

    public void setAsMandatoryInput(List<Task> asMandatoryInput) {
        this.asMandatoryInput = asMandatoryInput;
    }

    public List<Task> getAsOptionalInput() {
        return asOptionalInput;
    }

    public void setAsOptionalInput(List<Task> asOptionalInput) {
        this.asOptionalInput = asOptionalInput;
    }

    public List<Task> getAsOutput() {
        return asOutput;
    }

    public void setAsOutput(List<Task> asOutput) {
        this.asOutput = asOutput;
    }

    public List<Activity> getAsGuidanceWorkItem() {
        return asGuidanceWorkItem;
    }

    public void setAsGuidanceWorkItem(List<Activity> asGuidanceWorkItem) {
        this.asGuidanceWorkItem = asGuidanceWorkItem;
    }
}
