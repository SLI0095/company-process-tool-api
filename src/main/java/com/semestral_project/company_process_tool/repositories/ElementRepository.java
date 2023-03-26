package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElementRepository extends CrudRepository<Element, Long> {

    @Query("SELECT e FROM Element e WHERE (?1 MEMBER e.canEdit OR ?1 MEMBER e.hasAccess)")
    List<Element> findAllElementsTemplateForUser(User user);

    @Query("SELECT e FROM Element e WHERE (?1 MEMBER e.canEdit)")
    List<Element> findAllElementsTemplateForUserCanEdit(User user);

    @Query("SELECT e FROM Element e " +
            "LEFT JOIN e.canBeUsedIn AS elem " +
            "WHERE e.isTemplate = true OR :id = elem.id")
    List<Element> usableInProcessForUser(@Param("id") Long id);

    @Query("SELECT e FROM Element e " +
            "left JOIN e.project proj ON proj = :project " +
            "left JOIN e.canEdit ce  " +
            "left join e.canBeUsedIn AS p " +
            "WHERE (e.isTemplate = true OR :id = p.id) " +
            "AND (:user = e.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) ")
    List<Element> findUsableInProcessForUserCanEdit(@Param("id") Long id, @Param("user") User user,  @Param("project") Project project);
}
