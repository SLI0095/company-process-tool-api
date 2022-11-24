package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.UserType;
import com.semestral_project.company_process_tool.repositories.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserTypeService {

    @Autowired
    UserTypeRepository userTypeRepository;

    public List<UserType> getAllUsersAndGroups() {
        try {
            return (List<UserType>) userTypeRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public UserType getUserTypeById(long id) {
        Optional<UserType> userType = userTypeRepository.findById(id);
        return userType.orElse(null);
    }
}
