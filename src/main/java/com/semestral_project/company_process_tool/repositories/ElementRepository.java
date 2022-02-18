package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ElementRepository extends CrudRepository<Element, Long> {

    @Query("SELECT e FROM Element e WHERE e.project.id = ?1")
    List<Element> findAllElementsInProject(Long projectId);

    @Query("SELECT e FROM Element e WHERE e.project = NULL")
    List<Element> findAllElementsTemplate();
}
