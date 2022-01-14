package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.WorkItem;
import org.springframework.data.repository.CrudRepository;

public interface WorkItemRepository extends CrudRepository<WorkItem, Long> {
}
