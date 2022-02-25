package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProcessRepository extends CrudRepository<Process, Long> {
    @Query("SELECT p FROM Process p WHERE p.project = NULL")
    List<Process> findAllTemplatesProcesses();

    @Query("SELECT p FROM Process p WHERE p.project.id = ?1")
    List<Process> findAllProcessesInProject(Long projectId);

}
