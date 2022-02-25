package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
}
