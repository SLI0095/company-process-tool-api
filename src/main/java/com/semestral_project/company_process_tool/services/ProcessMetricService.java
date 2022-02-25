package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.ProcessMetric;
import com.semestral_project.company_process_tool.repositories.ProcessMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProcessMetricService {

    @Autowired
    ProcessMetricRepository processMetricRepository;

    public int updateMetric(long id, ProcessMetric metric){
        Optional<ProcessMetric> metricData = processMetricRepository.findById(id);

        if(metricData.isPresent()){
            ProcessMetric metric_ = metricData.get();
            metric_.setName(metric.getName());
            metric_.setDescription(metric.getDescription());

            processMetricRepository.save(metric_);
            return 1;
        }
        else
        {
            return 2;
        }
    }
}
