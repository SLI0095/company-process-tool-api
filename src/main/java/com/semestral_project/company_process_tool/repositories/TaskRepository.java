package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.Project;
import com.semestral_project.company_process_tool.entities.Task;
import com.semestral_project.company_process_tool.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE (t.project.id = ?1) AND (?2 IN (t.canEdit) OR ?2 IN (t.hasAccess))")
    List<Task> findAllTasksInProjectForUser(Long projectId, User user);

    @Query("SELECT t FROM Task t WHERE t.project = NULL AND (?1 IN (t.canEdit) OR ?1 IN (t.hasAccess))")
    List<Task> findAllTasksTemplatesForUser(User user);
}
