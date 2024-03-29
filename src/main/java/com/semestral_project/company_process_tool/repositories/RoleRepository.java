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
            "left JOIN r.canEdit ce  " +
            "left JOIN r.hasAccess ha " +
            "WHERE r.isTemplate = :isTemplate AND (:user = r.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator)))")
    List<Role> findByIsTemplateUserCanView(@Param("isTemplate") boolean isTemplate, @Param("user") User user);

    @Query("SELECT r FROM Role r " +
            "left JOIN r.canEdit ce  " +
            "left JOIN r.hasAccess ha " +
            "WHERE :user = r.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))")
    List<Role> findAllCanUserView(@Param("user") User user);

    @Query("SELECT r FROM Role r " +
            "left JOIN r.canEdit ce  " +
            "WHERE :user = r.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) ")
    List<Role> findAllCanUserEdit(@Param("user") User user);


    @Query("SELECT r FROM Role r " +
            "left JOIN r.canEdit ce  " +
            "left join r.canBeUsedIn AS t " +
            "WHERE (r.isTemplate = true OR :id = t.id) " +
            "AND (:user = r.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) ")
    List<Role> findUsableInTaskForUserCanEdit(@Param("id") Long id, @Param("user") User user);


}
