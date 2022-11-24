package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.entities.UserGroup;
import com.semestral_project.company_process_tool.repositories.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserGroupService {

    @Autowired
    UserService userService;

    @Autowired
    UserGroupRepository userGroupRepository;


    public long addUserGroup(UserGroup userGroup, long userId) {
        User creator = userService.getUserById(userId);
        if(creator == null){
            System.out.println("User does not exist");
            return -1;
        }
        userGroup.setCreator(creator);
        userGroup = userGroupRepository.save(userGroup);
        return userGroup.getId();
    }

    public List<UserGroup> getAllGroups(){
        try{
            return (List<UserGroup>) userGroupRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public UserGroup getGroupById(long id){
        Optional<UserGroup> group = userGroupRepository.findById(id);
        return group.orElse(null);
    }

    public void addUserToGroup(long groupId ,User user, long creatorId){
        UserGroup group = getGroupById(groupId);
        if(group == null){
            return;
        }
       User creator = userService.getUserById(creatorId);

        //TODO test if comparing works
       if(group.getCreator() != creator){
           return;
       }

       user = userService.getUserById(user.getId());
       if(user == null){
           return;
       }

       var users = group.getUsers();
       if(users.contains(user)){
           return;
       }
       users.add(user);
       group.setUsers(users);
       userGroupRepository.save(group);
    }

    public void removeUserFromGroup(long groupId ,User user, long creatorId){
        UserGroup group = getGroupById(groupId);
        if(group == null){
            return;
        }
        User creator = userService.getUserById(creatorId);

        //TODO test if comparing works
        if(group.getCreator() != creator){
            return;
        }

        user = userService.getUserById(user.getId());
        if(user == null){
            return;
        }

        var users = group.getUsers();
        if(!users.contains(user)){
            return;
        }
        users.remove(user);
        group.setUsers(users);
        userGroupRepository.save(group);
    }

}
