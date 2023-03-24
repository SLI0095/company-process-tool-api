package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends CrudRepository<State, Long> {
}
