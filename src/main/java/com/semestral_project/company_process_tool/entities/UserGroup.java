package com.semestral_project.company_process_tool.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("group")
public class UserGroup extends UserType{

    String groupName;

    @ManyToOne
    User creator;

    @ManyToMany
    @JoinTable(name = "group_user",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    List<User> users = new ArrayList<>();

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
