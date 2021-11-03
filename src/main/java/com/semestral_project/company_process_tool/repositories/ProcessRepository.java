package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.repository.CrudRepository;

public interface ProcessRepository extends CrudRepository<Process,Long> {
}
