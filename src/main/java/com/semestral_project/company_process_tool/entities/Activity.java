package com.semestral_project.company_process_tool.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("activity")
public class Activity extends Element{


    @ManyToMany(mappedBy = "partOfActivity")
    private List<Element> elements;






    public Activity() {
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
