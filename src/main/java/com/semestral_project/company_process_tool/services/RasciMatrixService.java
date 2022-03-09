package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RasciMatrixService {

    @Autowired
    ProcessRepository processRepository;

    public String[][] getMatrixForRender(Process process){
        List<Task> tasksInProcess = new ArrayList<>();
        List<Role> rolesInProcess = new ArrayList<>();

        for(Element e : process.getElements()){
            if (e instanceof Task){
                tasksInProcess.add((Task)e);
            }
        }
        for(Task t : tasksInProcess){
            for(Rasci r : t.getRasciList())
            {
                Role role = r.getRole();
                if(!rolesInProcess.contains(role)){
                    rolesInProcess.add(role);
                }
            }
        }


        String[][] returnMatrix = buildMatrix(tasksInProcess, rolesInProcess);

        return returnMatrix;
    }

    private String[][] buildMatrix(List<Task> tasks, List<Role> roles){
        int a = tasks.size() + 1;
        int b = roles.size() + 1;

        if(a == 1|| b == 1)
        {
            return new String[0][0];
        }

        String[][] returnMatrix = new String[a][b];
        for(int i = 0; i < a; i++){
            for(int j = 0; j < b; j++){
                if(i == 0){
                    if(j == 0){
                        returnMatrix[i][j] = "";
                    } else {
                        returnMatrix[i][j] = roles.get(j-1).getName(); // roles head
                    }
                } else {
                    if(j == 0){
                        returnMatrix[i][j] = tasks.get(i-1).getName(); // tasks
                    } else {
                        Role r = roles.get(j-1);
                        Task t = tasks.get(i-1);
                        String value = "-";
                        for(Rasci rasci : t.getRasciList()){
                            if(rasci.getRole().getId() == r.getId()){
                                char type = rasci.getType();
                                value = String.valueOf(type);
                                break;
                            }
                        }
                        returnMatrix[i][j] = value;
                    }
                }
            }
        }
        return returnMatrix;
    }

    public String[][] getMatrixForRenderInHtml(Process process){
        List<Task> tasksInProcess = new ArrayList<>();
        List<Role> rolesInProcess = new ArrayList<>();

        for(Element e : process.getElements()){
            if (e instanceof Task){
                tasksInProcess.add((Task)e);
            }
        }
        for(Task t : tasksInProcess){
            for(Rasci r : t.getRasciList())
            {
                Role role = r.getRole();
                if(!rolesInProcess.contains(role)){
                    rolesInProcess.add(role);
                }
            }
        }


        String[][] returnMatrix = buildMatrixHTML(tasksInProcess, rolesInProcess);

        return returnMatrix;
    }

    private String[][] buildMatrixHTML(List<Task> tasks, List<Role> roles){
        int a = tasks.size() + 1;
        int b = roles.size() + 1;

        if(a == 1|| b == 1)
        {
            return new String[0][0];
        }

        String[][] returnMatrix = new String[a][b];
        for(int i = 0; i < a; i++){
            for(int j = 0; j < b; j++){
                if(i == 0){
                    if(j == 0){
                        returnMatrix[i][j] = "";
                    } else {
                        returnMatrix[i][j] = "<a href='#role_" + roles.get(j-1).getId() + "'>" + roles.get(j-1).getName() + "</a>"; // roles head
                    }
                } else {
                    if(j == 0){
                        returnMatrix[i][j] = "<a href='#element_" + tasks.get(i-1).getId() + "'>" + tasks.get(i-1).getName() + "</a>"; // tasks
                    } else {
                        Role r = roles.get(j-1);
                        Task t = tasks.get(i-1);
                        String value = "-";
                        for(Rasci rasci : t.getRasciList()){
                            if(rasci.getRole().getId() == r.getId()){
                                char type = rasci.getType();
                                value = String.valueOf(type);
                                break;
                            }
                        }
                        returnMatrix[i][j] = value;
                    }
                }
            }
        }
        return returnMatrix;
    }
}
