package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.User;
import org.apache.tomcat.jni.Proc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProcessRepository extends CrudRepository<Process, Long> {

    @Query("SELECT p FROM Process p WHERE (?1 MEMBER p.canEdit OR ?1 MEMBER p.hasAccess)")
    List<Process> findAllTemplatesProcessesForUser(User user);

    @Query("SELECT p FROM Process p WHERE p.isTemplate = :isTemplate")
    List<Process> findByIsTemplate(@Param("isTemplate") boolean isTemplate);

    @Query("SELECT p FROM Process p WHERE (?1 MEMBER p.canEdit)")
    List<Process> findAllTemplatesProcessesForUserCanEdit(User user);

    @Query("SELECT p FROM Process p WHERE p.isTemplate = true OR :process MEMBER p.canBeUsedIn")
    List<Process> usableInProcessForUser(@Param("process") Process process);
}
