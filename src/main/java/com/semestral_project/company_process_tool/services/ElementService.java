package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.entities.WorkItem;
import com.semestral_project.company_process_tool.repositories.ElementRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import com.semestral_project.company_process_tool.utils.ItemUsersUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ElementService {

    @Autowired
    ElementRepository elementRepository;
    @Autowired
    UserService userService;
    @Autowired
    ProcessService processService;



    public List<Element> getAllElements(){
        try {
            return (List<Element>) elementRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Element getElementById(long id){
        Optional<Element> element = elementRepository.findById(id);
        return element.orElse(null);
    }

    public boolean elementExists(long id){
        return elementRepository.existsById(id);
    }

    public List<Element> getAllUserCanView(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Element> ret = new HashSet<>();
        List<Element> elements = (List<Element>) elementRepository.findAll();
        for(Element e : elements){
            if(ItemUsersUtil.getAllUsersCanView(e).contains(user)){
                ret.add(e);
            }
        }
        return new ArrayList<>(ret);

//        try {
//            if(userRepository.existsById(userId)) {
//                User user = userRepository.findById(userId).get();
//                return elementRepository.findAllElementsTemplateForUser(user);
//            } else return null;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return null;
//        }
    }

    public List<Element> getAllUserCanEdit(long userId){
        User user = userService.getUserById(userId);
        if(user == null){
            return new ArrayList<>();
        }
        HashSet<Element> ret = new HashSet<>();
        List<Element> elements = (List<Element>) elementRepository.findAll();
        for(Element e : elements){
            if(ItemUsersUtil.getAllCanEdit(e).contains(user)){
                ret.add(e);
            }
        }
        return new ArrayList<>(ret);

//        try {
//            if(userRepository.existsById(userId)) {
//                User user = userRepository.findById(userId).get();
//                return elementRepository.findAllElementsTemplateForUserCanEdit(user);
//            } else return null;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return null;
//        }
    }

    public List<Element> getUsableInProcessForUser(long userId, Long processId){
        User user = userService.getUserById(userId);
        if(user == null || !processService.processExists(processId)){
            return new ArrayList<>();
        }
        return elementRepository.findUsableInProcessForUserCanEdit(processId, user);
       /* process = processService.getProcessById(process.getId());
        if(process == null){
            return new ArrayList<>();
        }
        HashSet<Element> ret = new HashSet<>();
        List<Element> elements = elementRepository.usableInProcessForUser(process.getId());
        for(Element e : elements){
            if(ItemUsersUtil.getAllCanEdit(e).contains(user)){
                ret.add(e);
            }
        }
        return new ArrayList<>(ret);*/
    }
}
