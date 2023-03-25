package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.entities.State;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.StateRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import com.semestral_project.company_process_tool.utils.ItemUsersUtil;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StateService {

    @Autowired
    StateRepository stateRepository;
    @Autowired
    UserService userService;

    public List<State> getAllStates(){
        try {
            return (List<State>) stateRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean addState(State state){
        try {
            stateRepository.save(state);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public State getStateById(long id){
        Optional<State> stateData = stateRepository.findById(id);
        return stateData.orElse(null);
    }

    public int updateState(long id, State state, long whoEdits){
        State mainState = getStateById(id);
        if (mainState == null){
            return  2;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainState.getWorkItem()).contains(editor)){
            return 3;
        }
        mainState.setStateName(state.getStateName());
        mainState.setStateDescription(state.getStateDescription());
        stateRepository.save(mainState);
        return 1;
    }

}
