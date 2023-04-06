package cz.sli0095.promod.repositories;

import cz.sli0095.promod.entities.Element;
import cz.sli0095.promod.entities.Project;
import cz.sli0095.promod.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElementRepository extends CrudRepository<Element, Long> {

    @Query("SELECT distinct e FROM Element e " +
            "left JOIN e.project.canEdit ce  " +
            "left join e.canBeUsedIn AS p " +
            "WHERE (e.isTemplate = true OR :id = p.id) " +
            "AND (:user = e.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND e.project = :project")
    List<Element> findUsableInProcessForUserCanEdit(@Param("id") Long id, @Param("user") User user, @Param("project") Project project);

    @Query("SELECT distinct e FROM Element e " +
            "left JOIN e.canEdit ce  " +
            "left join e.canBeUsedIn AS p " +
            "WHERE (e.isTemplate = true OR :id = p.id) " +
            "AND (:user = e.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND e.project = null")
    List<Element> findUsableInProcessForUserCanEditInDefault(@Param("id") Long id, @Param("user") User user);
}
