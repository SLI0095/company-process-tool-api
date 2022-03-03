package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElementRepository extends CrudRepository<Element, Long> {

    @Query("SELECT e FROM Element e WHERE e.project.id = ?1 AND (?2 IN (e.canEdit) OR ?2 IN (e.hasAccess))")
    List<Element> findAllElementsInProjectForUser(Long projectId, User user);

    @Query("SELECT e FROM Element e WHERE e.project = NULL AND (?1 IN (e.canEdit) OR ?1 IN (e.hasAccess))")
    List<Element> findAllElementsTemplateForUser(User user);
}
