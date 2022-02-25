package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.WorkItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkItemRepository extends CrudRepository<WorkItem, Long> {

    @Query("SELECT w FROM WorkItem w WHERE w.project.id = ?1")
    List<WorkItem> findAllWorkItemsInProject(Long projectId);

    @Query("SELECT w FROM WorkItem w WHERE w.project = NULL")
    List<WorkItem> findAllWorkItemTemplate();
}
