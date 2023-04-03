package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotElement;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotRole;
import com.semestral_project.company_process_tool.repositories.RoleRepository;
import com.semestral_project.company_process_tool.services.configurations.ConfigurationHelper;
import com.semestral_project.company_process_tool.services.configurations.ConfigurationRoleService;
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

    @Autowired
    TaskService taskService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ConfigurationRoleService configurationRoleService;

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
        if(role.getProject() != null){
            Project project = projectService.getProjectById(role.getProject().getId());
            if(project == null) {
                return -1;
            }
            if(!ItemUsersUtil.getAllUsersCanEdit(project).contains(owner)){
                return -2;
            }
            role.setOwner(project.getProjectOwner());
        } else {
            role.setOwner(owner);
        }
        role.setOwner(owner);
        role = roleRepository.save(role);
        return role.getId();
    }

    public List<Task> getUsableIn(long id){
        Role role = getRoleById(id);
        if(role == null){
            return null;
        }
        return role.getCanBeUsedIn();
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
        if(role.getHasAccess().contains(access) || role.getOwner() == access){
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
    }

    public int updateIsTemplate(long id, boolean isTemplate, long whoEdits) {
        Role mainRole = getRoleById(id);
        if (mainRole == null) {
            return 2;
        }
        User editor = userService.getUserById(whoEdits);
        if (editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainRole).contains(editor)) {
            return 3;
        }
        mainRole.setTemplate(isTemplate);
        roleRepository.save(mainRole);
        return 1;
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
        for(SnapshotRole snapshot : role.getSnapshots()){
            snapshot.setOriginalRole(null);
        }
        for(Item i : role.getConfigurations()){
            Role r = (Role) i;
            r.setCreatedFrom(null);
            roleRepository.save(r);
        }
        roleRepository.delete(role);
        return 1;

    }

//    public List<Role> getAllUserCanView(long userId){
//        User user = userService.getUserById(userId);
//        if(user == null){
//            return new ArrayList<>();
//        }
//        return roleRepository.findAllCanUserView(user);
//    }

    public List<Role> getAllUserCanView(long userId, Long projectId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return roleRepository.findAllCanUserViewInDefault(user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return roleRepository.findAllCanUserView(user, project);
    }

//    public List<Role> getAllUserCanEdit(long userId){
//        User user = userService.getUserById(userId);
//        if(user == null){
//            return new ArrayList<>();
//        }
//        return roleRepository.findAllCanUserEdit(user);
//    }

    public List<Role> getAllUserCanEdit(long userId, Long projectId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return roleRepository.findAllCanUserEditInDefault(user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return roleRepository.findAllCanUserEdit(user, project);
    }

//    public List<Role> getAllUserCanViewByTemplate(long userId, boolean isTemplate){
//        User user = userService.getUserById(userId);
//        if(user == null){
//            return new ArrayList<>();
//        }
//        return roleRepository.findByIsTemplateUserCanView(isTemplate,user);
//    }

    public List<Role> getAllUserCanViewByTemplate(long userId, boolean isTemplate, Long projectId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return roleRepository.findByIsTemplateUserCanViewInDefault(isTemplate, user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return roleRepository.findByIsTemplateUserCanView(isTemplate,user, project);
    }

//    public List<Role> getUsableInForUser(long userId, long taskId){
//        User user = userService.getUserById(userId);
//        if(user == null || !taskService.taskExists(taskId)){
//            return new ArrayList<>();
//        }
//        return roleRepository.findUsableInTaskForUserCanEdit(taskId, user);
//    }

    public List<Role> getUsableInForUser(long userId, long taskId, Long projectId){
        User user = userService.getUserById(userId);
        if(user == null || !taskService.taskExists(taskId)){
            return new ArrayList<>();
        }
        if(projectId == -1){
            return roleRepository.findUsableInTaskForUserCanEditInDefault(taskId, user);
        }
        Project project = projectService.getProjectById(projectId);
        if(project == null){
            return new ArrayList<>();
        }
        return roleRepository.findUsableInTaskForUserCanEdit(taskId, user, project);
    }

    public int addUsableIn(long taskId, long user,  Task task) {
        Role role = getRoleById(taskId);
        if(role == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(user);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(role).contains(editor)){
            return 5; //cannot edit
        }
        task = taskService.getTaskById(task.getId());
        if(!ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 5;
        }
        var list =  role.getCanBeUsedIn();
        if(list.contains(task)){
            return 3;
        }
        list.add(task);
        role.setCanBeUsedIn(list);
        roleRepository.save(role);
        return 1;
    }

    public int removeUsableIn(long taskId, long user,  Task task) {
        Role role = getRoleById(taskId);
        if(role == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(user);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(role).contains(editor)){
            return 5; //cannot edit
        }
        task = taskService.getTaskById(task.getId());
        if(!ItemUsersUtil.getAllUsersCanEdit(task).contains(editor)){
            return 5;
        }
        var list =  role.getCanBeUsedIn();
        if(!list.contains(task)){
            return 3;
        }
        list.remove(task);
        role.setCanBeUsedIn(list);
        roleRepository.save(role);
        return 1;
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

    public Role revertRole(long userId, SnapshotRole snapshot) {
        snapshot = snapshotRoleService.getSnapshotRoleById(snapshot.getId());
        if(snapshot == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        Role role = getRoleById(snapshot.getOriginalId());
        if(role == null){
            return null;
        }
        if(!ItemUsersUtil.getAllUsersCanEdit(role).contains(user)){
            return null;
        }
        return snapshotRoleService.revertRoleFromSnapshot(snapshot,new SnapshotsHelper());
    }

    public Role createNewConfiguration(long userId, long roleId, long projectId) {
        Role role = getRoleById(roleId);
        if(role == null){
            return null;
        }
        User user = userService.getUserById(userId);
        if(user == null){
            return null;
        }
        if(projectId == -1){
            return configurationRoleService.createNewConfiguration(role, new ConfigurationHelper(), user, null);
        }
        Project project = projectService.getProjectById(projectId);
        if(project != null && ItemUsersUtil.getAllUsersCanEdit(project).contains(user)){
            return configurationRoleService.createNewConfiguration(role, new ConfigurationHelper(),  user, project);
        }
        return null;
    }

    public int changeOwner(long id, long editorId, long newOwnerId){
        Role role = getRoleById(id);
        if(role == null){
            return 2;
        }
        User editor = userService.getUserById(editorId);
        if(editor == null){
            return 3;
        }
        User newOwner = userService.getUserById(newOwnerId);
        if(newOwner == null){
            return 3;
        }
        if(role.getOwner().getId() != editor.getId()){
            return 4; //MUST BE OWNER TO CHANGE OWNER
        }
        role.setOwner(newOwner);
        roleRepository.save(role);
        return 1;
    }
}
