package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
}
