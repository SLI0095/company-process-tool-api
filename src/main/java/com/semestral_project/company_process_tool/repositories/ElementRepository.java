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

    @Query("SELECT e FROM Element e WHERE (?1 MEMBER e.canEdit OR ?1 MEMBER e.hasAccess)")
    List<Element> findAllElementsTemplateForUser(User user);

    @Query("SELECT e FROM Element e WHERE (?1 MEMBER e.canEdit)")
    List<Element> findAllElementsTemplateForUserCanEdit(User user);
}
