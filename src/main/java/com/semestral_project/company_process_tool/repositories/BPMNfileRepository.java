package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.BPMNfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BPMNfileRepository extends CrudRepository<BPMNfile, Long> {
}
