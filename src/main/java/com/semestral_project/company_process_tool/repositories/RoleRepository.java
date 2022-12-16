package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("SELECT r FROM Role r " +
            "WHERE :user MEMBER r.canEdit OR :user MEMBER r.hasAccess OR :user = r.owner")
    List<Role> findAllRolesTemplatesForUser(@Param("user") User user);

    @Query("SELECT r FROM Role r " +
            "WHERE r.isTemplate = :isTemplate")
    List<Role> findByIsTemplate(@Param("isTemplate") boolean isTemplate);



    /*@Query("SELECT r FROM Role r " +
            "JOIN r.canEdit ce ON TYPE(ce) = UserGroup " +
            "JOIN r.hasAccess ha ON TYPE(ha) = UserGroup " +
            "WHERE :user MEMBER ce.users OR :user = ce.creator " +
            "OR :user MEMBER ha.users OR :user = ha.creator " +
            "UNION " +
            "FROM Role r2 " +
            "JOIN r2.canEdit ce2 ON TYPE(ce2) = User " +
            "JOIN r2.hasAccess ha2 ON TYPE(ha2) = User " +
            "WHERE :user = ce2 OR " +
            ":user = ha2 " +
            "UNION " +
            "FROM Role r3 " +
            "WHERE :user = r3.owner")
    List<Role> findAllCanUserView(@Param("user") User user);*/

    //@Query("SELECT DISTINCT(r) FROM Role,  r WHERE (?1 MEMBER r.canEdit OR ?1 MEMBER r.hasAccess OR ?1 = r.owner)")
//    @Query("SELECT r FROM Role r " +
//            "JOIN r.canEdit ce ON TYPE(ce) = 'UserGroup'  " +
//            "JOIN r.hasAccess ha ON TYPE(ce) = 'UserGroup' " +
//            "WHERE (:user MEMBER r.canEdit OR :user MEMBER r.hasAccess " +
//            "OR :user = r.owner " +
//            "OR :user MEMBER ce.users OR :user = ce.creator " +
//            "OR  :user MEMBER ha.users OR :user = ha.creator)")

    @Query("SELECT r FROM Role r " +
            "WHERE :user MEMBER r.canEdit OR :user = r.owner")
    List<Role> findAllTasksTemplatesForUserCanEdit(User user);

    @Query("SELECT r FROM Role r WHERE r.isTemplate = true OR :task MEMBER r.canBeUsedIn")
    List<Role> usableInTaskForUser(@Param("task") Task task);


}
