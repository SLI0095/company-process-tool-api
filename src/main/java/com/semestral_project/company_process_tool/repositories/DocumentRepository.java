package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.Document;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, Long> {
}
