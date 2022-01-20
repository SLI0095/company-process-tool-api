package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.State;
import com.semestral_project.company_process_tool.repositories.StateRepository;
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

        if(stateData.isPresent()) {
            return stateData.get();
        }
        else return null;
    }

    public int updateState(long id, State state){
        Optional<State> stateData = stateRepository.findById(id);

        if(stateData.isPresent()){
            State state_ = stateData.get();
            state_.setStateName(state.getStateName());
            state_.setStateDescription(state.getStateDescription());

            stateRepository.save(state_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

}
