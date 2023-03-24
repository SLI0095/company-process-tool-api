package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.ProcessMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessMetricRepository extends CrudRepository<ProcessMetric, Long> {
}
