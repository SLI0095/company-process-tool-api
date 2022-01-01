package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.DocumentOld;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<DocumentOld,Long> {
}
