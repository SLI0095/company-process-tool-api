package cz.sli0095.promod.services;

import cz.sli0095.promod.entities.State;
import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.utils.ItemUsersUtil;
import cz.sli0095.promod.repositories.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
