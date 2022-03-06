package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Project;
import com.semestral_project.company_process_tool.entities.Task;
import com.semestral_project.company_process_tool.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE ?1 MEMBER p.canEdit OR ?1 MEMBER p.hasAccess")
    List<Project> findAllProjectsForUser(User user);
}
