package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE (?1 MEMBER t.canEdit OR ?1 MEMBER t.hasAccess)")
    List<Task> findAllTasksTemplatesForUser(User user);

    @Query("SELECT t FROM Task t WHERE t.isTemplate = :isTemplate")
    List<Task> findByIsTemplate(@Param("isTemplate") boolean isTemplate);

    @Query("SELECT t FROM Task t WHERE (?1 MEMBER t.canEdit)")
    List<Task> findAllTasksTemplatesForUserCanEdit(User user);

    @Query("SELECT t FROM Task t " +
            "LEFT JOIN t.canBeUsedIn AS p " +
            "WHERE t.isTemplate = true OR :id = p.id")
    List<Task> usableInProcessForUser(@Param("id") Long id);





    @Query("SELECT t FROM Task t " +
            "left JOIN t.canEdit ce  " +
            "left JOIN t.hasAccess ha " +
            "WHERE t.isTemplate = :isTemplate AND (:user = t.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator)))")
    List<Task> findByIsTemplateUserCanView(@Param("isTemplate") boolean isTemplate, @Param("user") User user);

    @Query("SELECT t FROM Task t " +
            "left JOIN t.canEdit ce  " +
            "left JOIN t.hasAccess ha " +
            "WHERE :user = t.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))")
    List<Task> findAllCanUserView(@Param("user") User user);

    @Query("SELECT t FROM Task t " +
            "left JOIN t.canEdit ce  " +
            "WHERE :user = t.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) ")
    List<Task> findAllCanUserEdit(@Param("user") User user);


    @Query("SELECT t FROM Task t " +
            "left JOIN t.canEdit ce  " +
            "left join t.canBeUsedIn AS p " +
            "WHERE (t.isTemplate = true OR :id = p.id) " +
            "AND (:user = t.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) ")
    List<Task> findUsableInProcessForUserCanEdit(@Param("id") Long id, @Param("user") User user);
}
