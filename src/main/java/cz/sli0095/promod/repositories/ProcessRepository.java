package cz.sli0095.promod.repositories;

import cz.sli0095.promod.entities.Process;
import cz.sli0095.promod.entities.Project;
import cz.sli0095.promod.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessRepository extends CrudRepository<Process, Long> {
    @Query("SELECT distinct p FROM Process p " +
            "left JOIN p.canEdit ce  " +
            "left JOIN p.hasAccess ha " +
            "WHERE p.isTemplate = :isTemplate AND (:user = p.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND p.project = null")
    List<Process> findByIsTemplateUserCanViewInDefault(@Param("isTemplate") boolean isTemplate, @Param("user") User user);

    @Query("SELECT distinct p FROM Process p " +
            "left JOIN p.canEdit ce  " +
            "left JOIN p.hasAccess ha " +
            "WHERE (:user = p.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND p.project = null")
    List<Process> findAllCanUserViewInDefault(@Param("user") User user);

    @Query("SELECT distinct p FROM Process p " +
            "left JOIN p.canEdit ce  " +
            "WHERE (:user = p.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND p.project = null")
    List<Process> findAllCanUserEditInDefault(@Param("user") User user);


    @Query("SELECT distinct p FROM Process p " +
            "left JOIN p.project.canEdit ce  " +
            "left JOIN p.project.hasAccess ha " +
            "WHERE (:user = p.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND p.project = :project")
    List<Process> findAllCanUserView(@Param("user") User user,  @Param("project") Project project);

    @Query("SELECT distinct p FROM Process p " +
            "left JOIN p.project.canEdit ce  " +
            "WHERE (:user = p.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND p.project = :project")
    List<Process> findAllCanUserEdit(@Param("user") User user, @Param("project") Project project);

    @Query("SELECT distinct p FROM Process p " +
            "left JOIN p.project.canEdit ce  " +
            "left JOIN p.project.hasAccess ha " +
            "WHERE p.isTemplate = :isTemplate AND (:user = p.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND p.project = :project")
    List<Process> findByIsTemplateUserCanView(@Param("isTemplate") boolean isTemplate,@Param("user") User user, @Param("project") Project project);
}