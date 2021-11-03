package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.LoginCredentials;
import com.semestral_project.company_process_tool.repositories.LoginCredentialsRepository;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class LoginCredentialsController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final LoginCredentialsRepository loginCredentialsRepository;

    public LoginCredentialsController(LoginCredentialsRepository loginCredentialsRepository) {
        this.loginCredentialsRepository = loginCredentialsRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody LoginCredentials credentials)
    {
        if(loginCredentialsRepository.findByUsername(credentials.getUsername()).isPresent())
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("User already exists"));
        }
        else
        {
            try
            {
                credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
                loginCredentialsRepository.save(credentials);
                return ResponseEntity.ok(new ResponseMessage("User registered"));
            }
            catch (Exception e)
            {
                return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
            }

        }
    }



    @GetMapping("/login")
    public ResponseEntity<ResponseMessage> checkLogin(@RequestBody LoginCredentials credentials)
    {
        Optional<LoginCredentials> credentialsData = loginCredentialsRepository.findByUsername(credentials.getUsername());
        if(credentialsData.isPresent())
        {
            LoginCredentials credentials_ = credentialsData.get();
            if(passwordEncoder.matches(credentials.getPassword(),credentials_.getPassword()))
            {
                return ResponseEntity.ok(new ResponseMessage("Login OK"));
            }
            else
            {
                return ResponseEntity.badRequest().body(new ResponseMessage("Bad password"));
            }
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("User not registered"));
        }
    }
}
