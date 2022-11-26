package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotRole;
import com.semestral_project.company_process_tool.repositories.RoleRepository;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotRoleService;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotsHelper;
import com.semestral_project.company_process_tool.utils.ItemUsersUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    SnapshotRoleService snapshotRoleService;

    @Autowired
    UserService userService;

    @Autowired
    UserTypeService userTypeService;


    public Role fillRole(Role oldRole, Role updateRole){
        oldRole.setName(updateRole.getName());
        oldRole.setBriefDescription(updateRole.getBriefDescription());
        oldRole.setMainDescription(updateRole.getMainDescription());
        oldRole.setVersion(updateRole.getVersion());
        oldRole.setChangeDate(updateRole.getChangeDate());
        oldRole.setChangeDescription(updateRole.getChangeDescription());
        oldRole.setSkills(updateRole.getSkills());
        oldRole.setAssignmentApproaches(updateRole.getAssignmentApproaches());
        return oldRole;
    }

    public List<Role> getAllRoles(){
        try {
            return (List<Role>) roleRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Role getRoleById(long id){
        Optional<Role> roleData = roleRepository.findById(id);
        return roleData.orElse(null);
    }

    public long addRole(Role role, long userId){
        User owner = userService.getUserById(userId);
        if(owner == null){
            return -1;
        }
        role.setOwner(owner);
        role = roleRepository.save(role);
        return role.getId();
//        try {
//            if(userRepository.existsById(userId)) {
//                User user = userRepository.findById(userId).get();
//                var list = role.getCanEdit();
//                list.add(user);
//                role = roleRepository.save(role);
//                return role.getId();
//            }
//            else return -1;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return -1;
//        }
    }

    public int addAccess(long roleId, long whoEdits, UserType getAccess){
        Role role = getRoleById(roleId);
        if(role == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(role).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(getAccess.getId());
        if(access == null){
            return 5;
        }
        if(role.getHasAccess().contains(access)){
            return 3; //already has access
        }
        var list = role.getCanEdit();
        if(list.contains(access)){
            list.remove(access);
            role.setCanEdit(list);
        }
        list = role.getHasAccess();
        list.add(access);
        role.setHasAccess(list);
        roleRepository.save(role);
        return  1; //OK

//        Optional<Role> roleData = roleRepository.findById(roleId);
//        if(roleData.isPresent()) {
//            Role role_ = roleData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(role_.getCanEdit().contains(whoEdits_)){
//                User getAccess_ = userRepository.findById(getAccess.getId()).get();
//                if(role_.getHasAccess().contains(getAccess_)) {
//                    return 3; //already has access
//                }
//                if(role_.getCanEdit().contains(getAccess_)){
//                    var list = role_.getCanEdit();
//                    if(list.size() == 1){
//                        return 6; //cannot remove all edit rights
//                    }
//                    list.remove(getAccess_);
//                    role_.setCanEdit(list);
//                }
//                    var list = role_.getHasAccess();
//                    list.add(getAccess_);
//                    role_.setHasAccess(list);
//                    roleRepository.save(role_);
//                    return 1; //OK
//
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
    }

    public int removeAccess(long roleId, long whoEdits, UserType removeAccess){
        Role role = getRoleById(roleId);
        if(role == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(role).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(removeAccess.getId());
        if(access == null){
            return 5;
        }
        if(!role.getHasAccess().contains(access)){
            return 3; //nothing to remove
        }
        var list = role.getHasAccess();
        list.remove(access);
        role.setHasAccess(list);
        roleRepository.save(role);
        return  1; //OK


//        Optional<Role> roleData = roleRepository.findById(roleId);
//        if(roleData.isPresent()) {
//            Role role_ = roleData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(role_.getCanEdit().contains(whoEdits_)){
//                User getAccess_ = userRepository.findById(removeAccess.getId()).get();
//                if(role_.getHasAccess().contains(getAccess_)) {
//                    var list = role_.getHasAccess();
//                    list.remove(getAccess_);
//                    role_.setHasAccess(list);
//                    roleRepository.save(role_);
//                    return 1; //access removed
//                } else{
//                    return 3; //nothing to remove
//                }
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
    }

    public int removeEdit(long roleId, long whoEdits, UserType removeEdit){
        Role role = getRoleById(roleId);
        if(role == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(role).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(removeEdit.getId());
        if(edit == null){
            return 5;
        }
        if(!role.getCanEdit().contains(edit)){
            return 3; //nothing to remove
        }
        var list = role.getCanEdit();
        list.remove(edit);
        role.setCanEdit(list);
        roleRepository.save(role);
        return  1; //OK


//        Optional<Role> roleData = roleRepository.findById(roleId);
//        if(roleData.isPresent()) {
//            Role role_ = roleData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(role_.getCanEdit().contains(whoEdits_) ){
//                User removeEdit_ = userRepository.findById(removeEdit.getId()).get();
//                if(role_.getCanEdit().contains(removeEdit_)) {
//                    var list = role_.getCanEdit();
//                    if(list.size() == 1){
//                        return 6;
//                    }
//                    list.remove(removeEdit_);
//                    role_.setCanEdit(list);
//                    roleRepository.save(role_);
//                    return 1; //edit removed
//                } else{
//                    return 3; //nothing to remove
//                }
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
    }

    public int addEdit(long roleId, long whoEdits, UserType getEdit){
        Role role = getRoleById(roleId);
        if(role == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(role).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(getEdit.getId());
        if(edit == null){
            return 5;
        }
        if(role.getCanEdit().contains(edit) || role.getOwner() == edit){
            return 4; //already can edit
        }
        var list = role.getHasAccess();
        if(list.contains(edit)){
            list.remove(edit);
            role.setHasAccess(list);
        }
        list = role.getCanEdit();
        list.add(edit);
        role.setCanEdit(list);
        roleRepository.save(role);
        return  1; //OK

//        Optional<Role> roleData = roleRepository.findById(roleId);
//        if(roleData.isPresent()) {
//            Role role_ = roleData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(role_.getCanEdit().contains(whoEdits_)){
//                User getEdit_ = userRepository.findById(getEdit.getId()).get();
//                if(role_.getCanEdit().contains(getEdit_)){
//                    return 4; //already can edit
//                } else if(role_.getHasAccess().contains(getEdit_)) {
//                    var list = role_.getHasAccess();
//                    list.remove(getEdit_);
//                    role_.setHasAccess(list);
//                    list = role_.getCanEdit();
//                    list.add(getEdit_);
//                    role_.setCanEdit(list);
//                    roleRepository.save(role_);
//                    return 1; //OK
//                } else{
//                    var list = role_.getCanEdit();
//                    list.add(getEdit_);
//                    role_.setCanEdit(list);
//                    roleRepository.save(role_);
//                    return 1; //OK
//                }
//            }else return 5; //cannot edit
//        }
//        else
//        {
//            return 2; //role not found
//        }
    }

    public int updateRole(long id, Role role, long whoEdits){
        Role mainRole = getRoleById(id);
        if (mainRole == null){
            return  2;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainRole).contains(editor)){
            return 3;
        }
        mainRole = fillRole(mainRole, role);
        roleRepository.save(mainRole);
        return 1;

//        Optional<Role> roleData = roleRepository.findById(id);
//        if(roleData.isPresent()) {
//            Role role_ = roleData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(role_.getCanEdit().contains(whoEdits_)){
//                role_ = fillRole(role_, role);
//                roleRepository.save(role_);
//                return 1;
//            } else {
//                return 3; //cannot edit
//            }
//        }
//        else
//        {
//            return 2;
//        }
    }

    public int removeRoleById(long id, long whoEdits){
        Role role = getRoleById(id);
        if (role == null){
            return  2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(role).contains(editor)){
            return 3; //cannot edit
        }
        roleRepository.delete(role);
        return 1;

//
//
//        Optional<Role> roleData = roleRepository.findById(id);
//        if(roleData.isPresent()) {
//            Role role_ = roleData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(role_.getCanEdit().contains(whoEdits_)){
//                for(SnapshotRole snapshot : role_.getSnapshots()){
//                    snapshot.setBriefDescription(null);
//                }
//                roleRepository.delete(role_);
//                return 1;
//            } else {
//                return 3; //cannot edit
//            }
//        }
//        else
//        {
//            return 2; //role not found
//        }
    }

    public List<Role> getAllUserCanView(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Role> ret = new HashSet<>();
        List<Role> roles = (List<Role>) roleRepository.findAll();
        for(Role r : roles){
            if(ItemUsersUtil.getAllUsersCanView(r).contains(user)){
                ret.add(r);
            }
        }
        return new ArrayList<>(ret);
    }

    public List<Role> getAllUserCanEdit(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Role> ret = new HashSet<>();
        List<Role> roles = (List<Role>) roleRepository.findAll();
        for(Role r : roles){
            if(ItemUsersUtil.getAllUsersCanEdit(r).contains(user)){
                ret.add(r);
            }
        }
        return new ArrayList<>(ret);
    }

    public int createSnapshot(Long id, long userId, String description) {
        Role role = getRoleById(id);
        if(role == null){
            return 2;
        }
        User editor = userService.getUserById(userId);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(role).contains(editor)) {
            return 3;
        }
        snapshotRoleService.createSnapshotRole(role,description, new SnapshotsHelper());
        return 1;
    }

    public Role restoreRole(long userId, SnapshotRole snapshot) {
        snapshot = snapshotRoleService.getSnapshotRoleById(snapshot.getId());
        if(snapshot == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        return snapshotRoleService.restoreRoleFromSnapshot(snapshot,new SnapshotsHelper(), user);
    }
}
