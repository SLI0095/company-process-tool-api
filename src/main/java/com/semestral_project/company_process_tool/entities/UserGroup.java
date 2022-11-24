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

    public UserGroup() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getAllMembers(){
        List<User> allMembers = new ArrayList<>(this.getUsers());
        allMembers.add(this.getCreator());
        return allMembers;
    }
}
