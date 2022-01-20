package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    private Role fillRole(Role oldRole, Role updateRole){
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
        if(roleData.isPresent()) {
            return roleData.get();
        }
        else return null;
    }

    public boolean addRole(Role role){
        try {
            roleRepository.save(role);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public int updateRole(long id, Role role){
        Optional<Role> roleData = roleRepository.findById(id);
        if(roleData.isPresent()) {
            Role role_ = roleData.get();
            role_ = fillRole(role_, role);

            roleRepository.save(role_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    public boolean removeRoleById(long id){
        try {
            roleRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
