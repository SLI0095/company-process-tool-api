package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Activity;
import org.springframework.data.repository.CrudRepository;

public interface ActivityRepository extends CrudRepository<Activity,Long> {
}
