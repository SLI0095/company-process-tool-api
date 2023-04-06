package cz.sli0095.promod.services.configurations;

import cz.sli0095.promod.entities.Project;
import cz.sli0095.promod.entities.Role;
import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.repositories.RoleRepository;
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
        role.setCreatedFrom(defaultRole);

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
