package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.project.id = ?1 AND (?2 IN (r.canEdit) OR ?2 IN (r.hasAccess))")
    List<Role> findAllRolesInProjectForUser(Long projectId, User user);

    @Query("SELECT r FROM Role r WHERE r.project = NULL AND (?1 IN (r.canEdit) OR ?1 IN (r.hasAccess))")
    List<Role> findAllRolesTemplatesForUser(User user);


}
