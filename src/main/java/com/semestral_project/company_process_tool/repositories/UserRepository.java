package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
