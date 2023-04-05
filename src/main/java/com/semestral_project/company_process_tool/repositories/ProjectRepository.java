package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Project;
import com.semestral_project.company_process_tool.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    @Query("SELECT distinct p FROM Project p " +
            "left JOIN p.canEdit ce  " +
            "left JOIN p.hasAccess ha " +
            "WHERE :user = p.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))")
    List<Project> findAllCanUserView(@Param("user") User user);

    @Query("SELECT distinct p FROM Project p " +
            "left JOIN p.canEdit ce  " +
            "WHERE :user = p.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))")
    List<Project> findAllCanUserEdit(@Param("user") User user);

    @Query("SELECT distinct p FROM Project p " +
            "left JOIN p.hasAccess ha " +
            "WHERE ((type(ha) = User AND ha = :user) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND p NOT IN (" +
            "SELECT distinct p FROM Project p " +
            "left JOIN p.canEdit ce  " +
            "WHERE (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))" +
            ")")
    List<Project> findAllUserHasOnlyAccess(@Param("user") User user);

    @Query("SELECT distinct p FROM Project p " +
            "left JOIN p.canEdit ce  " +
            "WHERE ((type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND p NOT IN (" +
            "SELECT distinct p FROM Project p " +
            "WHERE :user = p.projectOwner" +
            ")")
    List<Project> findAllUserCanOnlyEdit(@Param("user") User user);

    @Query("SELECT distinct p FROM Project p " +
            "WHERE :user = p.projectOwner")
    List<Project> findUsersProjects(@Param("user") User user);
}
