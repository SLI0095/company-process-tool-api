package com.semestral_project.company_process_tool.utils;

import com.semestral_project.company_process_tool.entities.*;

import java.util.HashSet;
import java.util.stream.Collectors;

public final class ItemUsersUtil {

    public static HashSet<User> getAllUsersCanEdit(Item item){
        var allUsers = new HashSet<User>();
        //add solo users
        allUsers.addAll(item.getCanEdit().stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .collect(Collectors.toSet()));

        //add all users from group
        allUsers.addAll(item.getCanEdit().stream()
                .filter(UserGroup.class::isInstance)
                .flatMap(list -> ((UserGroup) list).getAllMembers().stream())
                .collect(Collectors.toSet()));
        allUsers.add(item.getOwner());
        return allUsers;
    }
    public static HashSet<User> getAllUsersCanView(Item item){
        var allUsers = new HashSet<User>();
        //add solo users, access
        allUsers.addAll(item.getHasAccess().stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .collect(Collectors.toSet()));

        //add all users from group, access
        allUsers.addAll(item.getHasAccess().stream()
                .filter(UserGroup.class::isInstance)
                .flatMap(list -> ((UserGroup) list).getAllMembers().stream())
                .collect(Collectors.toSet()));

        //add solo edit, access
        allUsers.addAll(item.getCanEdit().stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .collect(Collectors.toSet()));

        //add all users from group, edit
        allUsers.addAll(item.getCanEdit().stream()
                .filter(UserGroup.class::isInstance)
                .flatMap(list -> ((UserGroup) list).getAllMembers().stream())
                .collect(Collectors.toSet()));

        allUsers.add(item.getOwner());
        return allUsers;
    }

    public static HashSet<UserType> getAllCanEdit(Item item){
        var ret = new HashSet<>(item.getCanEdit());
        ret.add(item.getOwner());
        return ret;
    }
    public static HashSet<UserType> getAllHasAccess(Item item){
        var ret = new HashSet<>(item.getHasAccess());
        ret.add(item.getOwner());
        return ret;
    }
}
