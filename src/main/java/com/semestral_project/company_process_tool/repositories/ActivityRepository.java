package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.ActivityOld;
import org.springframework.data.repository.CrudRepository;

public interface ActivityRepository extends CrudRepository<ActivityOld,Long> {
}
