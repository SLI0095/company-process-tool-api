package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.WorkItem;
import com.semestral_project.company_process_tool.repositories.WorkItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkItemService {

    @Autowired
    WorkItemRepository workItemRepository;

    public List<WorkItem> getAllWorkItems(){
        try {
            return (List<WorkItem>) workItemRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
