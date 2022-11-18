package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotRole;
import com.semestral_project.company_process_tool.repositories.RoleRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotRoleService;
import com.semestral_project.company_process_tool.services.snaphsots.SnapshotsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SnapshotRoleService snapshotRoleService;


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
        try {
            if(userRepository.existsById(userId)) {
                User user = userRepository.findById(userId).get();
                var list = role.getCanEdit();
                list.add(user);
                role = roleRepository.save(role);
                return role.getId();
            }
            else return -1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public int addAccess(long roleId, long whoEdits, User getAccess){
        Optional<Role> roleData = roleRepository.findById(roleId);
        if(roleData.isPresent()) {
            Role role_ = roleData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(role_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(getAccess.getId()).get();
                if(role_.getHasAccess().contains(getAccess_)) {
                    return 3; //already has access
                }
                if(role_.getCanEdit().contains(getAccess_)){
                    var list = role_.getCanEdit();
                    if(list.size() == 1){
                        return 6; //cannot remove all edit rights
                    }
                    list.remove(getAccess_);
                    role_.setCanEdit(list);
                }
                    var list = role_.getHasAccess();
                    list.add(getAccess_);
                    role_.setHasAccess(list);
                    roleRepository.save(role_);
                    return 1; //OK

            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int removeAccess(long roleId, long whoEdits, User removeAccess){
        Optional<Role> roleData = roleRepository.findById(roleId);
        if(roleData.isPresent()) {
            Role role_ = roleData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(role_.getCanEdit().contains(whoEdits_)){
                User getAccess_ = userRepository.findById(removeAccess.getId()).get();
                if(role_.getHasAccess().contains(getAccess_)) {
                    var list = role_.getHasAccess();
                    list.remove(getAccess_);
                    role_.setHasAccess(list);
                    roleRepository.save(role_);
                    return 1; //access removed
                } else{
                    return 3; //nothing to remove
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int removeEdit(long roleId, long whoEdits, User removeEdit){
        Optional<Role> roleData = roleRepository.findById(roleId);
        if(roleData.isPresent()) {
            Role role_ = roleData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(role_.getCanEdit().contains(whoEdits_) ){
                User removeEdit_ = userRepository.findById(removeEdit.getId()).get();
                if(role_.getCanEdit().contains(removeEdit_)) {
                    var list = role_.getCanEdit();
                    if(list.size() == 1){
                        return 6;
                    }
                    list.remove(removeEdit_);
                    role_.setCanEdit(list);
                    roleRepository.save(role_);
                    return 1; //edit removed
                } else{
                    return 3; //nothing to remove
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int addEdit(long roleId, long whoEdits, User getEdit){
        Optional<Role> roleData = roleRepository.findById(roleId);
        if(roleData.isPresent()) {
            Role role_ = roleData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(role_.getCanEdit().contains(whoEdits_)){
                User getEdit_ = userRepository.findById(getEdit.getId()).get();
                if(role_.getCanEdit().contains(getEdit_)){
                    return 4; //already can edit
                } else if(role_.getHasAccess().contains(getEdit_)) {
                    var list = role_.getHasAccess();
                    list.remove(getEdit_);
                    role_.setHasAccess(list);
                    list = role_.getCanEdit();
                    list.add(getEdit_);
                    role_.setCanEdit(list);
                    roleRepository.save(role_);
                    return 1; //OK
                } else{
                    var list = role_.getCanEdit();
                    list.add(getEdit_);
                    role_.setCanEdit(list);
                    roleRepository.save(role_);
                    return 1; //OK
                }
            }else return 5; //cannot edit
        }
        else
        {
            return 2; //role not found
        }
    }

    public int updateRole(long id, Role role, long whoEdits){
        Optional<Role> roleData = roleRepository.findById(id);
        if(roleData.isPresent()) {
            Role role_ = roleData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(role_.getCanEdit().contains(whoEdits_)){
                role_ = fillRole(role_, role);
                roleRepository.save(role_);
                return 1;
            } else {
                return 3; //cannot edit
            }
        }
        else
        {
            return 2;
        }
    }

    public int removeRoleById(long id, long whoEdits){
        Optional<Role> roleData = roleRepository.findById(id);
        if(roleData.isPresent()) {
            Role role_ = roleData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(role_.getCanEdit().contains(whoEdits_)){
                for(SnapshotRole snapshot : role_.getSnapshots()){
                    snapshot.setBriefDescription(null);
                }
                roleRepository.delete(role_);
                return 1;
            } else {
                return 3; //cannot edit
            }
        }
        else
        {
            return 2; //role not found
        }
    }

    public List<Role> getAllTemplatesForUser(long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            return roleRepository.findAllRolesTemplatesForUser(user);
        }
        else return null;
    }

    public List<Role> getAllTemplatesForUserCanEdit(long userId){
        if(userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            return roleRepository.findAllTasksTemplatesForUserCanEdit(user);
        }
        else return null;
    }

    public int createSnapshot(Long id, long userId, String description) {
        Optional<Role> roleData = roleRepository.findById(id);
        if(roleData.isPresent()) {
            Role role_ = roleData.get();
            User whoEdits_ = userRepository.findById(userId).get();
            if(role_.getCanEdit().contains(whoEdits_)){
                snapshotRoleService.createSnapshotRole(role_,description, new SnapshotsHelper());
                return 1;
            } else {
                return 3; //cannot edit
            }
        }
        else
        {
            return 2;
        }
    }

    public Role restoreRole(long userId, SnapshotRole snapshot) {
        snapshot =snapshotRoleService.getSnapshotRoleById(snapshot.getId());
        if(snapshot == null){
            return null;
        }
        User user = userRepository.findById(userId).get();
        return snapshotRoleService.restoreRoleFromSnapshot(snapshot,new SnapshotsHelper(), user);
    }
}
