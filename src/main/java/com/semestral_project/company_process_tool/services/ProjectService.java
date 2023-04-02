package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.ProjectRepository;
import com.semestral_project.company_process_tool.repositories.UserTypeRepository;
import com.semestral_project.company_process_tool.utils.ItemUsersUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserTypeService userTypeService;

    public List<Project> getAllProjects(){
        try {
            return (List<Project>) projectRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean projectExists(long id){
        return projectRepository.existsById(id);
    }

    public long addProject(Project project, long userId) {
        User owner = userService.getUserById(userId);
        if (owner == null) {
            return -1;
        }
        project.setProjectOwner(owner);
        project = projectRepository.save(project);
        return project.getId();
    }

    public Project getProjectById(long id){
        Optional<Project> projectData = projectRepository.findById(id);
        return projectData.orElse(null);
    }

    public boolean canAccessProject(long id, long userId){
        Project p = getProjectById(id);
        if(p == null){
            return false;
        }
        User user = userService.getUserById(userId);
        if(user == null) {
            return false;
        }
        return ItemUsersUtil.getAllUsersCanView(p).contains(user);
    }

    public List<Project> getAllUserCanView(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        return projectRepository.findAllCanUserView(user);
    }

    public List<Project> getAllUserCanEdit(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        return projectRepository.findAllCanUserEdit(user);
    }

    public int addAccess(long projectId, long whoEdits, UserType getAccess){
        Project project = getProjectById(projectId);
        if(project == null){
            return 2; //project not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(project).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(getAccess.getId());
        if(access == null){
            return 5;
        }
        if(project.getHasAccess().contains(access) || project.getProjectOwner() == access){
            return 3; //already has access
        }
        var list = project.getCanEdit();
        if(list.contains(access)){
            list.remove(access);
            project.setCanEdit(list);
        }
        list = project.getHasAccess();
        list.add(access);
        project.setHasAccess(list);
        projectRepository.save(project);
        return  1; //OK
    }

    public int removeAccess(long projectId, long whoEdits, UserType removeAccess){
        Project project = getProjectById(projectId);
        if(project == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(project).contains(editor)){
            return 5; //cannot edit
        }
        UserType access = userTypeService.getUserTypeById(removeAccess.getId());
        if(access == null){
            return 5;
        }
        if(!project.getHasAccess().contains(access)){
            return 3; //nothing to remove
        }
        var list = project.getHasAccess();
        list.remove(access);
        project.setHasAccess(list);
        projectRepository.save(project);
        return  1; //OK
    }

    public int removeEdit(long projectId, long whoEdits, UserType removeEdit){
        Project project = getProjectById(projectId);
        if(project == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(project).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(removeEdit.getId());
        if(edit == null){
            return 5;
        }
        if(!project.getCanEdit().contains(edit)){
            return 3; //nothing to remove
        }
        var list = project.getCanEdit();
        list.remove(edit);
        project.setCanEdit(list);
        projectRepository.save(project);
        return  1; //OK
    }

    public int addEdit(long projectId, long whoEdits, UserType getEdit){
        Project project = getProjectById(projectId);
        if(project == null){
            return 2; //role not found
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(project).contains(editor)){
            return 5; //cannot edit
        }
        UserType edit = userTypeService.getUserTypeById(getEdit.getId());
        if(edit == null){
            return 5;
        }
        if(project.getCanEdit().contains(edit) || project.getProjectOwner() == edit){
            return 4; //already can edit
        }
        var list = project.getHasAccess();
        if(list.contains(edit)){
            list.remove(edit);
            project.setHasAccess(list);
        }
        list = project.getCanEdit();
        list.add(edit);
        project.setCanEdit(list);
        projectRepository.save(project);
        return  1; //OK
    }

    public int updateProject(Long id, Project project, long userId) {
        Project mainProject = getProjectById(id);
        if (mainProject == null){
            return  2;
        }
        User editor = userService.getUserById(userId);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainProject).contains(editor)){
            return 3;
        }
        mainProject.setName(project.getName());
        mainProject.setBriefDescription(project.getBriefDescription());
        projectRepository.save(mainProject);
        return 1;
    }

    public int removeProject(Long id, long userId) {
        //TODO add body, remove project only owner and also remove all items with this project
        return 1;
    }
}
