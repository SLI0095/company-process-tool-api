package cz.sli0095.promod.repositories;

import cz.sli0095.promod.entities.Project;
import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.entities.WorkItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkItemRepository extends CrudRepository<WorkItem, Long> {
    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "left JOIN w.hasAccess ha " +
            "WHERE w.isTemplate = :isTemplate AND (:user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND w.project = null")
    List<WorkItem> findByIsTemplateUserCanViewInDefault(@Param("isTemplate") boolean isTemplate, @Param("user") User user);

    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "left JOIN w.hasAccess ha " +
            "WHERE (:user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND w.project = null")
    List<WorkItem> findAllCanUserViewInDefault(@Param("user") User user);

    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "WHERE (:user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND w.project = null")
    List<WorkItem> findAllCanUserEditInDefault(@Param("user") User user);


    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "left join w.canBeUsedIn AS t " +
            "WHERE (w.isTemplate = true OR :id = t.id) " +
            "AND (:user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND w.project = null")
    List<WorkItem> findUsableInTaskForUserCanEditInDefault(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.canEdit ce  " +
            "left join w.canBeUsedInProcesses AS p " +
            "WHERE (w.isTemplate = true OR :id = p.id) " +
            "AND (:user = w.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND w.project = null")
    List<WorkItem> findUsableInProcessForUserCanEditInDefault(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.project.canEdit ce  " +
            "left JOIN w.project.hasAccess ha " +
            "WHERE w.isTemplate = :isTemplate AND (:user = w.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND w.project = :project")
    List<WorkItem> findByIsTemplateUserCanView(@Param("isTemplate") boolean isTemplate, @Param("user") User user, @Param("project") Project project);

    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.project.canEdit ce  " +
            "left JOIN w.project.hasAccess ha " +
            "WHERE (:user = w.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) " +
            "AND w.project = :project")
    List<WorkItem> findAllCanUserView(@Param("user") User user,  @Param("project") Project project);

    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.project.canEdit ce  " +
            "WHERE (:user = w.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND w.project = :project")
    List<WorkItem> findAllCanUserEdit(@Param("user") User user,  @Param("project") Project project);


    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.project.canEdit ce  " +
            "left join w.canBeUsedIn AS t " +
            "WHERE (w.isTemplate = true OR :id = t.id) " +
            "AND (:user = w.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND w.project = :project")
    List<WorkItem> findUsableInTaskForUserCanEdit(@Param("id") Long id, @Param("user") User user,  @Param("project") Project project);

    @Query("SELECT distinct w FROM WorkItem w " +
            "left JOIN w.project.canEdit ce  " +
            "left join w.canBeUsedInProcesses AS p " +
            "WHERE (w.isTemplate = true OR :id = p.id) " +
            "AND (:user = w.project.projectOwner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator))) " +
            "AND w.project = :project")
    List<WorkItem> findUsableInProcessForUserCanEdit(@Param("id") Long id, @Param("user") User user,  @Param("project") Project project);
}
