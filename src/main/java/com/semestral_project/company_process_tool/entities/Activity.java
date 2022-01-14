package com.semestral_project.company_process_tool.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("activity")
public class Activity extends Element{


    @ManyToMany(mappedBy = "partOfActivity")
    private List<Element> elements;

    @ManyToMany(mappedBy = "asGuidanceWorkItem")
    private List<WorkItem> guidanceWorkItems;

    @OneToMany(mappedBy = "element")
    private List<Rasci> rasciList;


    public Activity() {
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public List<WorkItem> getGuidanceWorkItems() {
        return guidanceWorkItems;
    }

    public void setGuidanceWorkItems(List<WorkItem> guidanceWorkItems) {
        this.guidanceWorkItems = guidanceWorkItems;
    }

    public List<Rasci> getRasciList() {
        return rasciList;
    }

    public void setRasciList(List<Rasci> rasciList) {
        this.rasciList = rasciList;
    }
}
