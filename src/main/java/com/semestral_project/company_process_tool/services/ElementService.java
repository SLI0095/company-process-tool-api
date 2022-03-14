package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.ElementRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElementService {

    @Autowired
    ElementRepository elementRepository;
    @Autowired
    UserRepository userRepository;

    public List<Element> getAllElements(){
        try {
            return (List<Element>) elementRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Element> getAllTemplatesForUser(long userId){
        try {
            if(userRepository.existsById(userId)) {
                User user = userRepository.findById(userId).get();
                return elementRepository.findAllElementsTemplateForUser(user);
            } else return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Element> getAllTemplatesForUserCanEdit(long userId){
        try {
            if(userRepository.existsById(userId)) {
                User user = userRepository.findById(userId).get();
                return elementRepository.findAllElementsTemplateForUserCanEdit(user);
            } else return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
