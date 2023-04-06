package cz.sli0095.promod.repositories;

import cz.sli0095.promod.entities.Project;
import cz.sli0095.promod.entities.Task;
import cz.sli0095.promod.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
        @Query("SELECT distinct t FROM Task t " +
                "left JOIN t.canEdit ce  " +
                "left JOIN t.hasAccess ha " +
                "WHERE t.isTemplate = :isTemplate AND (:user = t.owner OR (type(ce) = User AND ce = :user) " +
                "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
                "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
                "AND t.project = null")
    List<Task> findByIsTemplateUserCanViewInDefault(@Param("isTemplate") boolean isTemplate, @Param("user") User user);

    @Query("SELECT distinct t FROM Task t " +
            "left JOIN t.canEdit ce  " +
            "left JOIN t.hasAccess ha " +
            "WHERE (:user = t.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND t.project = null")
    List<Task> findAllCanUserViewInDefault(@Param("user") User user);

    @Query("SELECT distinct t FROM Task t " +
            "left JOIN t.canEdit ce  " +
            "WHERE (:user = t.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND t.project = null")
    List<Task> findAllCanUserEditInDefault(@Param("user") User user);

    @Query("SELECT distinct t FROM Task t " +
            "left JOIN t.project.canEdit ce  " +
            "left JOIN t.project.hasAccess ha " +
            "WHERE t.isTemplate = :isTemplate AND (:user = t.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND t.project = :project")
    List<Task> findByIsTemplateUserCanView(@Param("isTemplate") boolean isTemplate, @Param("user") User user, @Param("project") Project project);

    @Query("SELECT distinct t FROM Task t " +
            "left JOIN t.project.canEdit ce  " +
            "left JOIN t.project. hasAccess ha " +
            "WHERE (:user = t.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND t.project = :project")
    List<Task> findAllCanUserView(@Param("user") User user, @Param("project") Project project);

    @Query("SELECT distinct t FROM Task t " +
            "left JOIN t.project.canEdit ce  " +
            "WHERE (:user = t.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND t.project = :project")
    List<Task> findAllCanUserEdit(@Param("user") User user, @Param("project") Project project);

}
