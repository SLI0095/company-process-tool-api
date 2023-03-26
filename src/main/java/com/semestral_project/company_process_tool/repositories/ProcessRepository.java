package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
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




    @Query("SELECT p FROM Process p " +
            "left JOIN p.project proj ON proj = :project " +
            "left JOIN p.canEdit ce  " +
            "left JOIN p.hasAccess ha " +
            "WHERE p.isTemplate = :isTemplate AND (:user = p.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))) ")
    List<Process> findByIsTemplateUserCanView(@Param("isTemplate") boolean isTemplate, @Param("user") User user, @Param("project") Project project);

    @Query("SELECT p FROM Process p " +
            "left JOIN p.project proj ON proj = :project " +
            "left JOIN p.canEdit ce  " +
            "left JOIN p.hasAccess ha " +
            "WHERE :user = p.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ha) = User AND ha = :user) OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) " +
            "OR (type(ha) = UserGroup AND (:user MEMBER ha.users OR :user = ha.creator))")
    List<Process> findAllCanUserView(@Param("user") User user, @Param("project") Project project);

    @Query("SELECT p FROM Process p " +
            "left JOIN p.project proj ON proj = :project " +
            "left JOIN p.canEdit ce  " +
            "WHERE :user = p.owner OR (type(ce) = User AND ce = :user) " +
            "OR (type(ce) = UserGroup AND (:user MEMBER ce.users OR :user = ce.creator)) ")
    List<Process> findAllCanUserEdit(@Param("user") User user, @Param("project") Project project);

}
