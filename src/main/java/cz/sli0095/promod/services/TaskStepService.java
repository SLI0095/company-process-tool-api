package cz.sli0095.promod.services;

import cz.sli0095.promod.entities.TaskStep;
import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.utils.ItemUsersUtil;
import cz.sli0095.promod.repositories.TaskStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskStepService {

    @Autowired
    TaskStepRepository taskStepRepository;
    @Autowired
    UserService userService;

    public TaskStep getTaskStepById(long id){
        Optional<TaskStep> taskStepData = taskStepRepository.findById(id);
        return taskStepData.orElse(null);
    }


    public int updateStep(long id, TaskStep step, long whoEdits){
        TaskStep mainStep = getTaskStepById(id);
        if (mainStep == null){
            return  2;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainStep.getTask()).contains(editor)){
            return 3;
        }
        mainStep.setName(step.getName());
        mainStep.setDescription(step.getDescription());
        taskStepRepository.save(mainStep);
        return 1;
    }
}
