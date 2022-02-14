package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProcessRepository processRepository;
    @Autowired
    TaskRepository taskRepository;


    private Project fillProject(Project oldProject, Project updatedProject){
        oldProject.setName(updatedProject.getName());
        oldProject.setBriefDescription(updatedProject.getBriefDescription());
        oldProject.setMainDescription(updatedProject.getMainDescription());
        oldProject.setVersion(updatedProject.getVersion());
        oldProject.setChangeDate(updatedProject.getChangeDate());
        oldProject.setChangeDescription(updatedProject.getChangeDescription());
        return oldProject;
    }

    public List<Project> getAllProjects(){
        try {
            return (List<Project>) projectRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Project getProjectById(long id){
        Optional<Project> projectData = projectRepository.findById(id);

        if(projectData.isPresent()) {
            return projectData.get();
        }
        else return null;
    }

    public boolean addProject(Project project){
        try {
            projectRepository.save(project);
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteProject(long id){
        try {
            projectRepository.deleteById(id);
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int updateProject(long id, Project project){
        Optional<Project> projectData = projectRepository.findById(id);
        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            project_ = fillProject(project_, project);
            projectRepository.save(project_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public List<Element> getAllElementsInProject(long projectId){
        Optional<Project> projectData = projectRepository.findById(projectId);

        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            return project_.getElements();
        }
        else return null;

    }

    public List<WorkItem> getAllWorkItemInProject(long projectId){
        Optional<Project> projectData = projectRepository.findById(projectId);

        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            return project_.getWorkItems();
        }
        else return null;
    }

    public List<Role> getAllRolesInProject(long projectId){
        Optional<Project> projectData = projectRepository.findById(projectId);

        if(projectData.isPresent()) {
            Project project_ = projectData.get();
            return project_.getRoles();
        }
        else return null;
    }
    public List<Process> getAllProcessesInProject(long projectId){
        return processRepository.findAllProcessesInProject(projectId);
    }

    public List<Task> getAllTasksInProject(long projectId){
        return taskRepository.findAllTasksInProject(projectId);
    }


}
