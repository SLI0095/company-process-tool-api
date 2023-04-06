package cz.sli0095.promod.services;

import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.repositories.UserRepository;
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

    @Autowired
    ProjectService projectService;

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
        if(userData.isEmpty())
        {
            return -1;
        }
        User user_ = userData.get();
        if(passwordEncoder.matches(user.getPassword(),user_.getPassword()))
        {
            return user_.getId();
        }
        return 0;
    }

    public List<User> getAllUser(){
        return (List<User>) userRepository.findAll();
    }

    public User getUserById(long id){
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User getUserByIdWithProjects(long id){
        Optional<User> userData = userRepository.findById(id);
        User user = userData.orElse(null);
        if(user == null){
            return null;
        }
        user.setCanEditProjects(projectService.getUserProjectsOnlyEdit(id));
        user.setHasAccessProjects(projectService.getUserProjectsOnlyAccess(id));
        return user;
    }

}
