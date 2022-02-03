package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface ProcessRepository extends CrudRepository<Process, Long> {
    @Query("SELECT p FROM element WHERE e.isTemplate = true")
    List<Process> findAllTemplates();
}
