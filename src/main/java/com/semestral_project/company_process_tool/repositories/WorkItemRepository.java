package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.Task;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.entities.WorkItem;
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

    @Query("SELECT w FROM WorkItem w WHERE w.isTemplate = true OR :task MEMBER w.canBeUsedIn")
    List<WorkItem> usableInTaskForUser(@Param("task") Task task);

    @Query("SELECT w FROM WorkItem w WHERE w.isTemplate = true OR :process MEMBER w.canBeUsedInProcesses")
    List<WorkItem> usableInProcessForUser(@Param("process") Process process);
}
