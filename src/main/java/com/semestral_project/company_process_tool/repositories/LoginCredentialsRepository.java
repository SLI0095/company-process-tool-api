package com.semestral_project.company_process_tool.repositories;

import com.semestral_project.company_process_tool.entities.LoginCredentials;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LoginCredentialsRepository extends CrudRepository<LoginCredentials,Long> {

    @Query("SELECT login FROM LoginCredentials login WHERE login.username = ?1")
    Optional<LoginCredentials> findByUsername(String username);
}
