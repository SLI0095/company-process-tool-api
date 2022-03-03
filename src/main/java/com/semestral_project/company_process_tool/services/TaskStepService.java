package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.TaskStep;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.TaskStepRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskStepService {

    @Autowired
    TaskStepRepository taskStepRepository;
    @Autowired
    UserRepository userRepository;

    public int updateStep(long id, TaskStep step, long whoEdits){
        Optional<TaskStep> stepData = taskStepRepository.findById(id);

        if(stepData.isPresent()){
            TaskStep step_ = stepData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(step_.getTask().getCanEdit().contains(whoEdits_)) {
                step_.setName(step.getName());
                step_.setDescription(step.getDescription());

                taskStepRepository.save(step_);
                return 1;
            }
            return 3;
        }
        else
        {
            return 2;
        }
    }
}
