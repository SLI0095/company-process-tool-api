package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.entities.WorkItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.project.id = ?1")
    List<Role> findAllRolesInProject(Long projectId);

    @Query("SELECT r FROM Role r WHERE r.project = NULL")
    List<Role> findAllRolesTemplates();

}
