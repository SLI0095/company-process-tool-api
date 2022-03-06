package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProcessRepository extends CrudRepository<Process, Long> {

    @Query("SELECT p FROM Process p WHERE p.project = NULL AND (?1 MEMBER p.canEdit OR ?1 MEMBER p.hasAccess)")
    List<Process> findAllTemplatesProcessesForUser(User user);

    @Query("SELECT p FROM Process p WHERE p.project.id = ?1 AND (?2 MEMBER p.canEdit OR ?2 MEMBER p.hasAccess)")
    List<Process> findAllProcessesInProjectForUser(Long projectId, User user);

}
