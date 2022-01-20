package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.TaskStep;
import com.semestral_project.company_process_tool.repositories.TaskStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskStepService {

    @Autowired
    TaskStepRepository taskStepRepository;

    public int updateStep(long id, TaskStep step){
        Optional<TaskStep> stepData = taskStepRepository.findById(id);

        if(stepData.isPresent()){
            TaskStep step_ = stepData.get();
            step_.setName(step.getName());
            step_.setDescription(step.getDescription());

            taskStepRepository.save(step_);
            return 1;
        }
        else
        {
            return 2;
        }
    }
}
