package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Task;
import com.semestral_project.company_process_tool.entities.User;
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
}
