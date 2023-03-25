package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkItemRepository extends CrudRepository<WorkItem, Long> {

    @Query("SELECT w FROM WorkItem w WHERE (?1 MEMBER w.canEdit OR ?1 MEMBER w.hasAccess)")
    List<WorkItem> findAllWorkItemTemplateForUser(User user);

    @Query("SELECT w FROM WorkItem w WHERE w.isTemplate = :isTemplate")
    List<WorkItem> findByIsTemplate(@Param("isTemplate") boolean isTemplate);

    @Query("SELECT w FROM WorkItem w WHERE (?1 MEMBER w.canEdit)")
    List<WorkItem> findAllWorkItemTemplateForUserCanEdit(User user);

    @Query("SELECT w FROM WorkItem w " +
            "LEFT JOIN w.canBeUsedIn as t " +
            "WHERE w.isTemplate = true OR :id = t.id")
    List<WorkItem> usableInTaskForUser(@Param("id") Long id);

    @Query("SELECT w FROM WorkItem w " +
            "LEFT JOIN w.canBeUsedInProcesses p " +
            "WHERE w.isTemplate = true OR :id = p.id")
    List<WorkItem> usableInProcessForUser(@Param("id") Long id);





    @Query("SELECT w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "left JOIN w.hasAccess ha " +
            "WHERE w.isTemplate = :isTemplate AND (:user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND w.project = :project")
    List<WorkItem> findByIsTemplateUserCanView(@Param("isTemplate") boolean isTemplate, @Param("user") User user, @Param("project") Project project);

    @Query("SELECT w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "left JOIN w.hasAccess ha " +
            "WHERE :user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator)) " +
            "AND w.project = :project")
    List<WorkItem> findAllCanUserView(@Param("user") User user,  @Param("project") Project project);

    @Query("SELECT w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "WHERE :user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "AND w.project = :project")
    List<WorkItem> findAllCanUserEdit(@Param("user") User user,  @Param("project") Project project);


    @Query("SELECT w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "left join w.canBeUsedIn AS t " +
            "WHERE (w.isTemplate = true OR :id = t.id) " +
            "AND (:user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND w.project = :project")
    List<WorkItem> findUsableInTaskForUserCanEdit(@Param("id") Long id, @Param("user") User user,  @Param("project") Project project);

    @Query("SELECT w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "left join w.canBeUsedInProcesses AS p " +
            "WHERE (w.isTemplate = true OR :id = p.id) " +
            "AND (:user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND w.project = :project")
    List<WorkItem> findUsableInProcessForUserCanEdit(@Param("id") Long id, @Param("user") User user,  @Param("project") Project project);
}
