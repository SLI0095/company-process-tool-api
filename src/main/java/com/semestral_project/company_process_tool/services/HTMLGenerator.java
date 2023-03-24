package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class HTMLGenerator {
    @Autowired
    ProcessService processService;
    @Autowired
    RasciMatrixService rasciMatrixService;

    private final List<Process> processList = new ArrayList<>();
    private List<Role> rolesToGenerate = new ArrayList<>();
    private List<Task> tasksToGenerate = new ArrayList<>();
    private List<WorkItem> workItemsToGenerate = new ArrayList<>();

    /*
    Process name
    Workflow image
    Process detail without name
    process metrics
    Elements in process - name with reference (subprocesses only info)
    Rasci matrix
    Detail of all tasks
        info
        steps
        work items
            inputs - reference
            outputs - reference
            guidance - reference
     Detail of all roles
        info
     Detail of all work items
        info
        states
        relations
     */

    public ZipOutputStream generateHTML(long processId, OutputStream stream) {
        try {
            Process process = processService.getProcessById(processId);
            this.fillList(process);
            for (Process p : processList) {
                String html = generateProcessHTML(p);
                String fileName = p.getName() + "_" + p.getId() + ".html";
                File htmlFile = new File("html/" + fileName);
                htmlFile.getParentFile().mkdirs();
                htmlFile.createNewFile();
                FileWriter myWriter = new FileWriter("html/" + fileName);
                myWriter.write(html);
                myWriter.close();
            }
            ZipOutputStream zipOutputStream = new ZipOutputStream(stream);
            addIndex(process,zipOutputStream);
            setupCss();

            File fileToZip = new File("html");
            zipFile(fileToZip, fileToZip.getName(), zipOutputStream);
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setupCss(){
        copyCss("src/main/resources/basic.css", "html/styles/main.css");
        copyCss("src/main/resources/basic.css", "html/styles/basic.css");
        copyCss("src/main/resources/pretty.css", "html/styles/pretty.css");
        copyCss("src/main/resources/template.css", "html/styles/template.css");
    }

    private void copyCss(String fromPath, String toPath){
        File from = new File(fromPath);
        File to = new File(toPath);
        try {
            to.getParentFile().mkdirs();
            to.createNewFile();
            copy(from,to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copy(File src, File dest) throws IOException{
        try (InputStream is = new FileInputStream(src); OutputStream os = new FileOutputStream(dest)) {
            // buffer size 1K
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buf)) > 0) {
                os.write(buf, 0, bytesRead);
            }
        }
    }


    private void addIndex(Process process, ZipOutputStream stream){
        try {
            File index = new File("index.html");
            index.createNewFile();
            FileWriter myWriter = new FileWriter("index.html");
            myWriter.write(String.format("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "   <head>\n" +
                    "      <title>HTML Meta Tag</title>\n" +
                    "      <meta http-equiv = \"refresh\" content = \"0; url = html/%s_%d.html\" />\n" +
                    "   </head>\n" +
                    "   <body>\n" +
                    "   </body>\n" +
                    "</html>", process.getName(), process.getId()));
            myWriter.close();
            FileInputStream fis = new FileInputStream(index);
            ZipEntry zipEntry = new ZipEntry(index.getName());
            stream.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                stream.write(bytes, 0, length);
            }
            fis.close();
            index.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            if(children == null){
                return;
            }
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            fileToZip.delete();
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
        fileToZip.delete();
    }

    private void fillList(Process process){
        processList.add(process);
        for(Element e : process.getElements()){
            if(e.getClass() == Process.class){
                fillList((Process) e);
            }
        }
    }


    private String generateProcessHTML(Process process){
        rolesToGenerate = new ArrayList<>();
        tasksToGenerate = new ArrayList<>();
        workItemsToGenerate = new ArrayList<>();
        StringBuilder htmlBuilder = new StringBuilder();

        String head = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<link rel=\"stylesheet\" href=\"https://unpkg.com/bpmn-js@9.0.2/dist/assets/bpmn-js.css\">\n" +
                "<link rel=\"stylesheet\" href=\"styles/main.css\">\n" +
                "<script src=\"https://unpkg.com/bpmn-js@9.0.2/dist/bpmn-navigated-viewer.development.js\"></script>\n" +
                "<body>";
        htmlBuilder.append(head);
        htmlBuilder.append("<h1 class='processName'>").append(process.getName()).append("</h1>");
        if(process.getWorkflow() != null){
            htmlBuilder.append(processWorkflow(process));
        }
        htmlBuilder.append(processDetail(process));
        htmlBuilder.append(processMetrics(process));
        htmlBuilder.append(processElements(process));
        htmlBuilder.append(processRasciMatrix(process));
        htmlBuilder.append("<h1 class='tasksHeading'>Tasks</h1>");
        for(Task t : tasksToGenerate){
            htmlBuilder.append("<div class='task'>");
            htmlBuilder.append(taskPart(t));
            htmlBuilder.append("</div>");
        }
        htmlBuilder.append("<h1 class='rolesHeading'>Roles</h1>");
        for(Role r : rolesToGenerate){
            htmlBuilder.append("<div class='role'>");
            htmlBuilder.append(rolePart(r));
            htmlBuilder.append("</div>");
        }
        htmlBuilder.append("<h1 class='workItemsHeading'>Work items</h1>");
        for(WorkItem w : workItemsToGenerate){
            htmlBuilder.append("<div class='workItem'>");
            htmlBuilder.append(workItemPart(w));
            htmlBuilder.append("</div>");
        }
        String footer = "</body>\n" +
                "</head>\n" +
                "</html>";
        htmlBuilder.append(footer);
        return htmlBuilder.toString();
    }



    private String processWorkflow(Process process){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div id=\"canvas\"></div>");
        returnString.append("<script>");
        String st = process.getWorkflow().getBpmnContent();
        st = st.replace("\n", "");
        returnString.append("var xml = ").append("'").append(st).append("';");
        returnString.append("var bpmnViewer = new BpmnJS({\n" +
                "        container: '#canvas'\n" +
                "      });");
        returnString.append("bpmnViewer.importXML(xml);\n");
        returnString.append("var canvas = bpmnViewer.get('canvas');\n");
        returnString.append("canvas.zoom('fit-viewport');\n");
        returnString.append("</script>");
        return returnString.toString();
    }

    private String processDetail(Process process){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='processDetails'>");
        returnString.append(generatePart("Brief description:", process.getBriefDescription()));
        returnString.append(generatePart("Main description:", process.getMainDescription()));
        returnString.append(generatePart("Purpose:", process.getPurpose()));
        returnString.append(generatePart("Scope:", process.getScope()));
        returnString.append(generatePart("Usage notes:", process.getUsageNotes()));
        returnString.append(generatePart("Alternatives:", process.getAlternatives()));
        returnString.append(generatePart("How to staff:", process.getHowToStaff()));
        returnString.append(generatePart("Key considerations:", process.getKeyConsiderations()));
        returnString.append(generatePart("Version:", process.getVersion()));
        if(process.getChangeDate() == null){
            returnString.append(generatePart("Change date:", "-" ));
        } else {
            returnString.append(generatePart("Change date:", process.getChangeDate().toString()));
        }
        returnString.append(generatePart("Change description:", process.getChangeDescription()));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String processMetrics(Process process){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='processMetrics'>");
        returnString.append("<h3>Process metrics</h3>");
        if(process.getMetrics().size() == 0){
            returnString.append("<p class='null'>-</p>");
        } else {
            returnString.append("<dl>");
            for(ProcessMetric metric : process.getMetrics()){
                returnString.append("<dt>").append(metric.getName()).append("</dt>");
                if(metric.getDescription() == null){
                    returnString.append("<dd>-</dd>");
                } else {
                    returnString.append("<dd>").append(metric.getDescription()).append("</dd>");
                }

            }
            returnString.append("</dl>");
        }
        returnString.append("</div>");
        return returnString.toString();
    }

    private String processElements(Process process){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='processActivities'>");
        returnString.append("<h3>Activities in process</h3>");
        returnString.append("<dl>");
        for(Long number : process.getElementsOrder()){
            for(Element e : process.getElements()){
                if(e.getId() != number){
                    continue;
                }
                if(e.getClass() == Task.class)
                {
                    returnString.append("<dt><a href='#element_").append(e.getId()).append("'>").append(e.getName()).append("</a></dt>");
                    this.tasksToGenerate.add((Task) e);
                } else {
                    returnString.append("<dt><a href='").append(e.getName()).append("_").append(e.getId()).append(".html'>").append(e.getName()).append("</a></dt>");
                }
                if(e.getBriefDescription() == null){
                    returnString.append("<dd>-</dd>");
                } else {
                    returnString.append("<dd>").append(e.getBriefDescription()).append("</dd>");
                }
            }
        }
        returnString.append("</dl>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String processRasciMatrix(Process process){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='processMatrix'>");
        returnString.append("<h3>RASCI matrix</h3>");
        returnString.append("<table><tbody>");
        String[][] matrix = rasciMatrixService.getMatrixForRenderInHtml(process);
        for(int i = 0; i < matrix.length; i++){
            returnString.append("<tr>");
            for(int j = 0; j < matrix[i].length; j++){
                if(i == 0){
                    returnString.append("<td>").append(matrix[i][j]).append("</td>"); //Roles head
                } else {
                    if(j == 0){
                        returnString.append("<td>").append(matrix[i][j]).append("</td>"); //tasks head
                    } else {
                        returnString.append("<td class='table").append(matrix[i][j]).append("'>").append(matrix[i][j]).append("</td>"); //tasks rasci
                    }
                }
            }
            returnString.append("</tr>");
        }
        returnString.append("</tbody></table>");
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskPart(Task task){
        StringBuilder returnString = new StringBuilder();
        for(Rasci rasci : task.getRasciList())
        {
            Role role = rasci.getRole();
            if(!rolesToGenerate.contains(role)){
                rolesToGenerate.add(role);
            }
        }
        returnString.append("<div id='element_").append(task.getId()).append("' >");
        returnString.append("<h2 class='taskName'>").append(task.getName()).append("</h2>");
        returnString.append(taskDetail(task));
        returnString.append(taskSteps(task));
        returnString.append(taskInputs(task));
        returnString.append(taskOutputs(task));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskDetail(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='taskDetail'>");
        returnString.append(generatePart("Brief description:", task.getBriefDescription()));
        returnString.append(generatePart("Main description:", task.getMainDescription()));
        returnString.append(generatePart("Purpose:", task.getPurpose()));
        returnString.append(generatePart("Key considerations:", task.getKeyConsiderations()));
        returnString.append(generatePart("Version:", task.getVersion()));
        if(task.getChangeDate() == null){
            returnString.append(generatePart("Change date:", "-" ));
        } else {
            returnString.append(generatePart("Change date:", task.getChangeDate().toString()));
        }
        returnString.append(generatePart("Change description:", task.getChangeDescription()));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskSteps(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='taskSteps'>");
        returnString.append("<h3>Task steps</h3>");
        if(task.getSteps().size() == 0){
            returnString.append("<p class='null'>-</p>");
        } else {
            returnString.append("<dl>");
            for(TaskStep step : task.getSteps()){
                returnString.append("<dt>").append(step.getName()).append("</dt>");
                if(step.getDescription() == null){
                    returnString.append("<dd>-</dd>");
                } else {
                    returnString.append("<dd>").append(step.getDescription()).append("</dd>");
                }
            }
            returnString.append("</dl>");
        }
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskInputs(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='taskInputs'>");
        returnString.append("<h3>Task inputs</h3>");
        if(task.getMandatoryInputs().size() == 0){
            returnString.append("<p class='null'>-</p>");
        } else {
            returnString.append("<dl>");
            for(WorkItem workItem : task.getMandatoryInputs()){
                if(!workItemsToGenerate.contains(workItem)){
                    workItemsToGenerate.add(workItem);
                }
                returnString.append("<dt><a href='#workItem_").append(workItem.getId()).append("'>").append(workItem.getName()).append("</a></dt>");
                if(workItem.getBriefDescription() == null){
                    returnString.append("<dd>-</dd>");
                } else {
                    returnString.append("<dd>").append(workItem.getBriefDescription()).append("</dd>");
                }
            }
            returnString.append("</dl>");
        }
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskOutputs(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='taskOutputs'>");
        returnString.append("<h3>Task outputs</h3>");
        if(task.getOutputs().size() == 0){
            returnString.append("<p class='null'>-</p>");
        } else {
            returnString.append("<dl>");
            for(WorkItem workItem : task.getOutputs()){
                if(!workItemsToGenerate.contains(workItem)){
                    workItemsToGenerate.add(workItem);
                }
                returnString.append("<dt><a href='#workItem_").append(workItem.getId()).append("'>").append(workItem.getName()).append("</a></dt>");
                if(workItem.getBriefDescription() == null){
                    returnString.append("<dd>-</dd>");
                } else {
                    returnString.append("<dd>").append(workItem.getBriefDescription()).append("</dd>");
                }
            }
            returnString.append("</dl>");
        }
        returnString.append("</div>");
        return returnString.toString();
    }

    private String taskGuidance(Task task){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='taskGuidance'>");
        returnString.append("<h3>Task guidance work items</h3>");
        if(task.getGuidanceWorkItems().size() == 0){
            returnString.append("<p class='null'>-</p>");
        } else {
            returnString.append("<dl>");
            for(WorkItem workItem : task.getGuidanceWorkItems()){
                if(!workItemsToGenerate.contains(workItem)){
                    workItemsToGenerate.add(workItem);
                }
                returnString.append("<dt><a href='#workItem_").append(workItem.getId()).append("'>").append(workItem.getName()).append("</a></dt>");
                if(workItem.getBriefDescription() == null){
                    returnString.append("<dd>-</dd>");
                } else {
                    returnString.append("<dd>").append(workItem.getBriefDescription()).append("</dd>");
                }
            }
            returnString.append("</dl>");
        }
        returnString.append("</div>");
        return returnString.toString();
    }

    private String rolePart(Role role){
        return "<div id='role_" + role.getId() + "' >" +
                "<h2 class='roleName'>" + role.getName() + "</h2>" +
                roleDetail(role) +
                "</div>";
    }

    private String roleDetail(Role role){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='roleDetail'>");
        returnString.append(generatePart("Brief description:", role.getBriefDescription()));
        returnString.append(generatePart("Main description:", role.getMainDescription()));
        returnString.append(generatePart("Skills:", role.getSkills()));
        returnString.append(generatePart("Assignment approaches:", role.getAssignmentApproaches()));
        returnString.append(generatePart("Version:", role.getVersion()));
        if(role.getChangeDate() == null){
            returnString.append(generatePart("Change date:", "-" ));
        } else {
            returnString.append(generatePart("Change date:", role.getChangeDate().toString()));
        }
        returnString.append(generatePart("Change description:", role.getChangeDescription()));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String workItemPart(WorkItem workItem){
        return "<div id='workItem_" + workItem.getId() + "' >" +
                "<h2 class='workItemName'>" + workItem.getName() + "</h2>" +
                workItemDetail(workItem) +
                workItemStates(workItem) +
                "</div>";
    }

    private String workItemDetail(WorkItem workItem){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='workItemDetail'>");
        returnString.append(generatePart("Brief description:", workItem.getBriefDescription()));
        returnString.append(generatePart("Main description:", workItem.getMainDescription()));
        returnString.append(generatePart("Work item type:", workItem.getWorkItemType()));
        returnString.append(generatePart("Source:", workItem.getUrlAddress()));
        returnString.append(generatePart("Purpose:", workItem.getPurpose()));
        returnString.append(generatePart("Key considerations:", workItem.getKeyConsiderations()));
        returnString.append(generatePart("Brief outline:", workItem.getBriefOutline()));
        returnString.append(generatePart("Notation:", workItem.getNotation()));
        returnString.append(generatePart("Impact of not having:", workItem.getImpactOfNotHaving()));
        returnString.append(generatePart("Reasons for not needing:", workItem.getReasonForNotNeeding()));
        returnString.append(generatePart("Template for work item:", workItem.getTemplateText()));
        returnString.append(generatePart("Version:", workItem.getVersion()));
        if(workItem.getChangeDate() == null){
            returnString.append(generatePart("Change date:", "-" ));
        } else {
            returnString.append(generatePart("Change date:", workItem.getChangeDate().toString()));
        }
        returnString.append(generatePart("Change description:", workItem.getChangeDescription()));
        returnString.append("</div>");
        return returnString.toString();
    }

    private String workItemStates(WorkItem workItem){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div class='workItemStates'>");
        returnString.append("<h3>Possible states</h3>");
        if(workItem.getWorkItemStates().size() == 0){
            returnString.append("<p class='null'>-</p>");
        } else {
            returnString.append("<dl>");
            for(State state : workItem.getWorkItemStates()){
                returnString.append("<dt>").append(state.getStateName()).append("</dt>");
                if(state.getStateDescription() == null){
                    returnString.append("<dd>-</dd>");
                } else {
                    returnString.append("<dd>").append(state.getStateDescription()).append("</dd>");
                }
            }
            returnString.append("</dl>");
        }
        returnString.append("</div>");
        return returnString.toString();
    }
    private String generatePart(String name, String content){
        StringBuilder returnString = new StringBuilder();
        returnString.append("<div>");
        returnString.append("<label>").append(name).append("</label>");
        if(content == null){
            returnString.append("<p>-</p>");
        } else {
            returnString.append("<p>").append(content).append("</p>");
        }
        returnString.append("</div>");

        return returnString.toString();
    }
}
