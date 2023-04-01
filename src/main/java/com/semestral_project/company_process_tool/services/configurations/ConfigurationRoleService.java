package com.semestral_project.company_process_tool.services.configurations;

import com.semestral_project.company_process_tool.entities.Project;
import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationRoleService {
    @Autowired
    RoleRepository roleRepository;

    public Role createNewConfiguration(Role defaultRole, ConfigurationHelper helper, User user, Project project){
        if(helper == null){
            helper = new ConfigurationHelper();
        }
        Role role = new Role();
        role.setName(defaultRole.getName());
        role.setBriefDescription(defaultRole.getBriefDescription());
        role.setMainDescription(defaultRole.getMainDescription());
        role.setAssignmentApproaches(defaultRole.getAssignmentApproaches());
        role.setSkills(defaultRole.getSkills());
        role.setChangeDate(defaultRole.getChangeDate());
        role.setChangeDescription(defaultRole.getChangeDescription());
        role.setVersion(defaultRole.getVersion());
        role.setTemplate(true);

        role.setProject(project);
        if (project == null){
            role.setOwner(user);
        } else {
            role.setOwner(project.getProjectOwner());
        }
        role = roleRepository.save(role);
        helper.addRole(defaultRole.getId(), role);
        return role;
    }
}
