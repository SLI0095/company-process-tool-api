package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.TaskStep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStepRepository extends CrudRepository<TaskStep, Long> {
}
