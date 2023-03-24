package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.State;
import com.semestral_project.company_process_tool.entities.TaskStep;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.TaskStepRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import com.semestral_project.company_process_tool.utils.ItemUsersUtil;
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

//        Optional<TaskStep> stepData = taskStepRepository.findById(id);
//
//        if(stepData.isPresent()){
//            TaskStep step_ = stepData.get();
//            User whoEdits_ = userRepository.findById(whoEdits).get();
//            if(step_.getTask().getCanEdit().contains(whoEdits_)) {
//                step_.setName(step.getName());
//                step_.setDescription(step.getDescription());
//
//                taskStepRepository.save(step_);
//                return 1;
//            }
//            return 3;
//        }
//        else
//        {
//            return 2;
//        }
    }
}
