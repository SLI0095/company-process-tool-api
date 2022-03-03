package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.ProcessMetric;
import com.semestral_project.company_process_tool.entities.User;
import com.semestral_project.company_process_tool.repositories.ProcessMetricRepository;
import com.semestral_project.company_process_tool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProcessMetricService {

    @Autowired
    ProcessMetricRepository processMetricRepository;
    @Autowired
    UserRepository userRepository;

    public int updateMetric(long id, ProcessMetric metric, long whoEdits){
        Optional<ProcessMetric> metricData = processMetricRepository.findById(id);

        if(metricData.isPresent()){
            ProcessMetric metric_ = metricData.get();
            User whoEdits_ = userRepository.findById(whoEdits).get();
            if(metric_.getProcess().getCanEdit().contains(whoEdits_)) {
                metric_.setName(metric.getName());
                metric_.setDescription(metric.getDescription());

                processMetricRepository.save(metric_);
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
