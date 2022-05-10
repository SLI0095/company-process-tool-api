package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE (?1 MEMBER r.canEdit OR ?1 MEMBER r.hasAccess)")
    List<Role> findAllRolesTemplatesForUser(User user);

    @Query("SELECT r FROM Role r WHERE (?1 MEMBER r.canEdit)")
    List<Role> findAllTasksTemplatesForUserCanEdit(User user);

}
