package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.Task;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.project.id = ?1")
    List<Task> findAllTasksInProject(Long projectId);

    @Query("SELECT t FROM Task t WHERE t.project = NULL")
    List<Task> findAllTasksTemplates();

}
