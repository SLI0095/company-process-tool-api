package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public int registerUser(User user){
        if(userRepository.findByUsername(user.getUsername()).isPresent())
        {
            return 3; //username already exists
        }
        else
        {
            try
            {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                return 1; //user registered
            }
            catch (Exception e)
            {
                return 2;
            }
        }
    }

    public long loginUser(User user){
        Optional<User> userData = userRepository.findByUsername(user.getUsername());
        if(userData.isPresent())
        {
            User user_ = userData.get();
            if(passwordEncoder.matches(user.getPassword(),user_.getPassword()))
            {
                return user_.getId();
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return -1;
        }
    }

    public List<User> getAllUser(){
        return (List<User>) userRepository.findAll();
    }

}
