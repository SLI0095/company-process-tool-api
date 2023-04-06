package cz.sli0095.promod.services;

import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.repositories.ProcessMetricRepository;
import cz.sli0095.promod.entities.ProcessMetric;
import cz.sli0095.promod.utils.ItemUsersUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProcessMetricService {

    @Autowired
    ProcessMetricRepository processMetricRepository;
    @Autowired
    UserService userService;


    public ProcessMetric getMetricById(long id){
        Optional<ProcessMetric> processMetric = processMetricRepository.findById(id);
        return processMetric.orElse(null);
    }

    public int updateMetric(long id, ProcessMetric metric, long whoEdits){
        ProcessMetric mainMetric = getMetricById(id);
        if (mainMetric == null){
            return  2;
        }
        User editor = userService.getUserById(whoEdits);
        if(editor == null || !ItemUsersUtil.getAllUsersCanEdit(mainMetric.getProcess()).contains(editor)){
            return 3;
        }
        mainMetric.setName(metric.getName());
        mainMetric.setDescription(metric.getDescription());
        processMetricRepository.save(mainMetric);
        return 1;
    }
}
