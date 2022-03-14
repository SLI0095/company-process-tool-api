package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.entities.WorkItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkItemRepository extends CrudRepository<WorkItem, Long> {

    @Query("SELECT w FROM WorkItem w WHERE w.project.id = ?1 AND (?2 MEMBER w.canEdit OR ?2 MEMBER w.hasAccess)")
    List<WorkItem> findAllWorkItemsInProjectForUser(Long projectId, User user);

    @Query("SELECT w FROM WorkItem w WHERE w.project = NULL AND (?1 MEMBER w.canEdit OR ?1 MEMBER w.hasAccess)")
    List<WorkItem> findAllWorkItemTemplateForUser(User user);

    @Query("SELECT w FROM WorkItem w WHERE w.project = NULL AND (?1 MEMBER w.canEdit)")
    List<WorkItem> findAllWorkItemTemplateForUserCanEdit(User user);
}
