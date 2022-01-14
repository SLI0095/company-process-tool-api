package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Artifact;
import org.springframework.data.repository.CrudRepository;

public interface ArtifactRepository extends CrudRepository<Artifact, Long> {
}
