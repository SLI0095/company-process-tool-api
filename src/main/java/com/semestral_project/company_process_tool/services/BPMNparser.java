package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.entities.Process;
import com.semestral_project.company_process_tool.repositories.*;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BPMNparser {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProcessRepository processRepository;
    @Autowired
    ElementRepository elementRepository;
    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    BPMNfileRepository bpmNfileRepository;
    @Autowired
    HistoryBPMNRepository historyBPMNRepository;
    @Autowired
    TaskService taskService;
    @Autowired
    ProcessService processService;


    private  List<Element> inXML;
    private final String[] bpmnElements = {
            "callActivity",
            "task",
            "sendTask",
            "receiveTask",
            "userTask",
            "manualTask",
            "serviceTask",
            "scriptTask",
            "businessRuleTask"};

    @Transactional
    public void saveBPMN(BPMNfile file, Process process) {
        inXML = new ArrayList<>();

        var bpmn_to_delete = process.getWorkflow();
        //if new BPMN is same as old change nothing
        if(bpmn_to_delete != null){
            if(bpmn_to_delete.getBpmnContent().equals(file.getBpmnContent())){
                return;
            }
        }
        file.setProcess(process);
        String bpmnContent = file.getBpmnContent();
        Project project = process.getProject();
        bpmnContent = this.newWorkItems(bpmnContent, project, process);
        bpmnContent = this.newProcesses(bpmnContent, project, process);
        bpmnContent = this.newTasks(bpmnContent, project, process);
        file.setProcess(process);
        file.setBpmnContent(bpmnContent);
        file = bpmNfileRepository.save(file);
        process.setWorkflow(file);
        processRepository.save(process);
        if(bpmn_to_delete != null){
            var bpmnHistory = new HistoryBPMN();
            bpmnHistory.setBpmnContent(bpmn_to_delete.getBpmnContent());
            bpmnHistory.setChangeDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            bpmnHistory.setProcess(process);
            historyBPMNRepository.save(bpmnHistory);
            bpmNfileRepository.delete(bpmn_to_delete);
        }
        this.updateProcesses(bpmnContent, process);
        this.updateTasks(bpmnContent, process);
        this.updateWorkItems(bpmnContent);
    }

    //@Transactional
    private String newWorkItems(String inputXML, Project project, Process process){
        String returnXML = inputXML;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check new Artifacts and Documents
            NodeList list = doc.getElementsByTagNameNS("*","dataObjectReference");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String oldId = element.getAttribute("id");
                    String name = element.getAttribute("name");

                    if (oldId.contains("WorkItem_new_")) { //Create new WorkItem
                        WorkItem w = new WorkItem();
                        String unchangedId = oldId.substring(12); //_DataObjectReference_....
                        w.setName(name);
                        w.setProject(project);
                        var listOfUsers = w.getCanEdit();
                        for(User u : process.getCanEdit()){
                            listOfUsers.add(u);
                        }
                        w.setCanEdit(listOfUsers);
                        listOfUsers = w.getHasAccess();
                        for(User u : process.getHasAccess()){
                            listOfUsers.add(u);
                        }
                        w.setHasAccess(listOfUsers);
                        //TODO add projectOwner as access and processOwner as access, change to call service

                        WorkItem savedWorkItem = workItemRepository.save(w);
                        String newId = "WorkItem_" + savedWorkItem.getId() + unchangedId;

                        returnXML = returnXML.replaceAll(oldId, newId);
                    }
                }
            }

            return returnXML;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return returnXML;
        }
    }

    //@Transactional
    private String newProcesses(String inputXML, Project project, Process process){
        String returnXML = inputXML;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check new Processes
            NodeList list = doc.getElementsByTagNameNS("*", "callActivity");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String oldId = element.getAttribute("id");
                    String name = element.getAttribute("name");

                    if (oldId.contains("Element_new_")) { //Create new Process
                        Process p = new Process();
                        String unchangedId = oldId.substring(11); //_Activity_....
                        p.setName(name);
                        p.setProject(project);
                        Process savedProcess = processRepository.save(p);
                        String newId = "Element_" + savedProcess.getId() + unchangedId;

                        returnXML = returnXML.replaceAll(oldId, newId);

                    }
                }
            }

            return returnXML;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return returnXML;
        }
    }

    private String newTasks(String inputXML, Project project, Process process){
        String returnXML = inputXML;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            returnXML = createNewTask(doc, returnXML, "task", project, process);
            returnXML = createNewTask(doc, returnXML, "sendTask", project, process);
            returnXML = createNewTask(doc, returnXML, "receiveTask", project, process);
            returnXML = createNewTask(doc, returnXML, "userTask", project, process);
            returnXML = createNewTask(doc, returnXML, "manualTask", project, process);
            returnXML = createNewTask(doc, returnXML, "serviceTask", project, process);
            returnXML = createNewTask(doc, returnXML, "scriptTask", project, process);
            returnXML = createNewTask(doc, returnXML, "businessRuleTask", project, process);

            return returnXML;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return returnXML;
        }
    }

    private String createNewTask(Document doc, String returnXML, String type, Project project, Process process){
        NodeList list = doc.getElementsByTagNameNS("*", type);
        for (int temp = 0; temp < list.getLength(); temp++) {

            Node node = list.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                String oldId = element.getAttribute("id");
                String name = element.getAttribute("name");

                if (oldId.contains("Element_new_")) { //Create new Process
                    Task t = new Task();
                    String unchangedId = oldId.substring(11); //_Activity_....
                    t.setName(name);
                    t.setTaskType(type);
                    t.setProject(project);
                    Task savedTask = taskRepository.save(t);
                    String newId = "Element_" + savedTask.getId() + unchangedId;

                    returnXML = returnXML.replaceAll(oldId, newId);
                }
            }
        }
        return returnXML;
    }

    //@Transactional
    private void updateProcesses(String inputXML, Process process){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check all Processes - callActivities in BPMN
            NodeList list = doc.getElementsByTagNameNS("*","callActivity");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String elementId = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    boolean needToSave = false;
                    boolean nameChanged = false;

                    if(Pattern.matches("Element_\\d+_.*", elementId)) {
                        Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                        Matcher m = p.matcher(elementId);

                        if(m.find()){
                            long process_id = Long.parseLong(m.group(1));
                            Process process1 = processRepository.findById(process_id).get();

                            if(!process1.getName().equals(name)){ //Check if name was changed
                                process1.setName(name);
                                needToSave = true;
                                nameChanged = true;
                            }

                            processService.addElementToProcess(process.getId(),process1);

//                            var isPartOf = process1.getPartOfProcess();
//                            if(isPartOf == null){
//                                isPartOf = new ArrayList<>();
//                            }
//                            if(!isPartOf.contains(process)){ //Check if is sub process already part of Process
//                                isPartOf.add(process);
//                                process1.setPartOfProcess(isPartOf);
//                                needToSave = true;
//                            }
                            if(needToSave){
                                elementRepository.save(process1);
                            }
                            if(nameChanged){
                                updateProcessInAllWorkflows(process1, true,process);
                            }
                            inXML.add(process1);
                        }
                    }
                }
            }

            List<Element> allElementsOfProcess = process.getElements();
            for(Element e : allElementsOfProcess){
                if(! inXML.contains(e)){
                    var list1 = e.getPartOfProcess();
                    list1.remove(process);
                    e.setPartOfProcess(list1);
                    elementRepository.save(e);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProcessInAllWorkflows(Process process, boolean nameChanged, Process alreadyChangedProcess) {

        var listOfProcesses = process.getPartOfProcess();
        boolean alreadyChanged = false;
        if(alreadyChangedProcess != null){
            alreadyChanged = true;
        }
        for (Process proc : listOfProcesses) { //Check all processes
            if(alreadyChanged){
                if (proc.getId() == alreadyChangedProcess.getId()) {
                    continue;
                }
            }
            BPMNfile workflow = proc.getWorkflow(); //check if process has saved workflow
            if(workflow == null){
                continue;
            }
            String XMLFile = workflow.getBpmnContent();
            var historyBPMN = new HistoryBPMN();
            historyBPMN.setProcess(workflow.getProcess());
            historyBPMN.setBpmnContent(XMLFile);
            historyBPMN.setChangeDate(LocalDateTime.now());

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));
                boolean saveNeeded = false;

                //Check previous type of task
                NodeList list = doc.getElementsByTagNameNS("*","callActivity");
                for (int temp = 0; temp < list.getLength(); temp++) {

                    Node node = list.item(temp);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String processId = element.getAttribute("id");
                        if (Pattern.matches("Element_\\d+_.*", processId)) {
                            Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                            Matcher m = p.matcher(processId);

                            if (m.find()) {
                                long foundId = Long.parseLong(m.group(1));
                                if (foundId == process.getId()) {
                                    if (nameChanged) {
                                        element.setAttribute("name", process.getName());
                                        saveNeeded = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if(saveNeeded){
                    String newXML = DocumentToString(doc);
                    workflow.setBpmnContent(newXML);
                    bpmNfileRepository.save(workflow);
                    historyBPMNRepository.save(historyBPMN);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void makeTaskUpdate(org.w3c.dom.Document doc, Process process, String type){
        NodeList list = doc.getElementsByTagNameNS("*", type);
        for (int temp = 0; temp < list.getLength(); temp++) {

            Node node = list.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                String elementId = element.getAttribute("id");
                String name = element.getAttribute("name");
                boolean needToSave = false;
                boolean nameChanged = false;
                boolean typeChanged = false;
                String oldTaskType = "";

                if(Pattern.matches("Element_\\d+_.*", elementId)) {
                    Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                    Matcher m = p.matcher(elementId);

                    if(m.find()){
                        long taskId = Long.parseLong(m.group(1));
                        Task task1 = taskRepository.findById(taskId).get();

                        if(!task1.getName().equals(name)){ //Check if name was changed
                            task1.setName(name);
                            nameChanged = true;
                            needToSave = true;
                        }
                        if(!task1.getTaskType().equals(type)){ //Check if type was changed
                            oldTaskType = task1.getTaskType();
                            task1.setTaskType(type);
                            needToSave = true;
                            typeChanged = true;
                        }

                        processService.addElementToProcess(process.getId(), task1);

//                        var isPartOf = task1.getPartOfProcess();
//                        if(isPartOf == null){
//                            isPartOf = new ArrayList<>();
//                        }
//                        if(!isPartOf.contains(process)){ //Check if task already part of Process
//                            isPartOf.add(process);
//                            task1.setPartOfProcess(isPartOf);
//                            needToSave = true;
//                        }

                        //Check added Inputs
                        NodeList listOfInputs = element.getElementsByTagNameNS("*","dataInputAssociation");
                        for (int temp2 = 0; temp2 < listOfInputs.getLength(); temp2++) {

                            Node node2 = listOfInputs.item(temp2);

                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                org.w3c.dom.Element elementInput = (org.w3c.dom.Element) node2;
                                if(elementInput.getElementsByTagNameNS("*","sourceRef").item(0) == null){
                                    continue;
                                }
                                String inputId = elementInput.getElementsByTagNameNS("*","sourceRef").item(0).getTextContent();
                                if (Pattern.matches("WorkItem_([0-9]+)_.*", inputId)) {
                                    Pattern p1 = Pattern.compile("WorkItem_([0-9]+)_.*");
                                    Matcher m1 = p1.matcher(inputId);

                                    if (m1.find()) {
                                        long workItemId = Long.parseLong(m1.group(1));
                                        WorkItem workItem = workItemRepository.findById(workItemId).get();

                                        taskService.addMandatoryInputWithoutUser(task1.getId(), workItem);
//                                        List<WorkItem> inputList = task1.getMandatoryInputs();
//                                        if(inputList == null){
//                                            inputList = new ArrayList<>();
//                                        }
//                                        if (!inputList.contains(workItem)) {
//                                            List<Task> tasksList = workItem.getAsMandatoryInput();
//                                            if(tasksList == null){
//                                                tasksList = new ArrayList<>();
//                                            }
//                                            tasksList.add(task1);
//                                            workItem.setAsMandatoryInput(tasksList);
//                                            workItemRepository.save(workItem);
//                                        }
                                    }
                                }
                            }
                        }

                        //Check added Outputs
                        NodeList listOfOutputs = element.getElementsByTagNameNS("*","dataOutputAssociation");
                        for (int temp2 = 0; temp2 < listOfOutputs.getLength(); temp2++) {

                            Node node2 = listOfOutputs.item(temp2);

                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                org.w3c.dom.Element elementOutput = (org.w3c.dom.Element) node2;
                                String outputId = elementOutput.getElementsByTagNameNS("*","targetRef").item(0).getTextContent();

                                if (Pattern.matches("WorkItem_([0-9]+)_.*", outputId)) {
                                    Pattern p1 = Pattern.compile("WorkItem_([0-9]+)_.*");
                                    Matcher m1 = p1.matcher(outputId);

                                    if (m1.find()) {
                                        long workItemId = Long.parseLong(m1.group(1));
                                        WorkItem workItem = workItemRepository.findById(workItemId).get();

                                        taskService.addOutputWithoutUser(task1.getId(), workItem);

//                                        List<WorkItem> outputList = task1.getOutputs();
//                                        if(outputList == null){
//                                            outputList = new ArrayList<>();
//                                        }
//                                        if (!outputList.contains(workItem)) {
//                                            List<Task> tasksList = workItem.getAsOutput();
//                                            if(tasksList == null){
//                                                tasksList = new ArrayList<>();
//                                            }
//                                            tasksList.add(task1);
//                                            workItem.setAsOutput(tasksList);
//                                            workItemRepository.save(workItem);
//                                        }
                                    }
                                }
                            }
                        }
                        if(needToSave){
                            taskRepository.save(task1);
                            this.updateTaskInAllWorkflows(task1, nameChanged,typeChanged,oldTaskType, process);
                        }
                        inXML.add(task1);
                    }
                }
            }
        }
    }

    //@Transactional
    private void updateTasks(String inputXML, Process process){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            makeTaskUpdate(doc,process,"task");
            makeTaskUpdate(doc,process,"sendTask");
            makeTaskUpdate(doc,process,"receiveTask");
            makeTaskUpdate(doc,process,"userTask");
            makeTaskUpdate(doc,process,"manualTask");
            makeTaskUpdate(doc,process,"serviceTask");
            makeTaskUpdate(doc,process,"scriptTask");
            makeTaskUpdate(doc,process,"businessRuleTask");
            //makeTaskUpdate(doc,process,"subProcess");

            List<Element> allElementsOfProcess = process.getElements();
            for(Element e : allElementsOfProcess) {
                if (!inXML.contains(e)) {
                    var list1 = e.getPartOfProcess();
                    list1.remove(process);
                    e.setPartOfProcess(list1);
                    elementRepository.save(e);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void updateTaskInAllWorkflows(Task task, boolean nameChanged, boolean typeChanged, String oldTaskType, Process alreadyChangedProcess) {

        var listOfProcesses = task.getPartOfProcess();
        boolean alreadyChanged = false;
        if(alreadyChangedProcess != null){
            alreadyChanged = true;
        }
        for (Process process : listOfProcesses) { //Check all processes
            if(alreadyChanged){
                if (process.getId() == alreadyChangedProcess.getId()) {
                    continue;
                }
            }
            BPMNfile workflow = process.getWorkflow();
            if(workflow == null){
                continue;
            }
            String XMLFile = workflow.getBpmnContent();
            var historyBPMN = new HistoryBPMN();
            historyBPMN.setProcess(workflow.getProcess());
            historyBPMN.setBpmnContent(XMLFile);
            historyBPMN.setChangeDate(LocalDateTime.now());

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));
                String tagName = task.getTaskType();
                boolean saveNeeded = false;

                if (typeChanged) {
                    tagName = oldTaskType;
                }

                //Check previous type of task
                NodeList list = doc.getElementsByTagNameNS("*", tagName);
                for (int temp = 0; temp < list.getLength(); temp++) {

                    Node node = list.item(temp);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String taskId = element.getAttribute("id");
                        if (Pattern.matches("Element_\\d+_.*", taskId)) {
                            Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                            Matcher m = p.matcher(taskId);

                            if (m.find()) {
                                long foundId = Long.parseLong(m.group(1));
                                if (foundId == task.getId()) {
                                    if (nameChanged) {
                                        element.setAttribute("name", task.getName());
                                        saveNeeded = true;
                                    }
                                    if (typeChanged) {
                                        doc.renameNode(node,"http://www.omg.org/spec/BPMN/20100524/MODEL", "bpmn:" + task.getTaskType());
                                        saveNeeded = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if(saveNeeded){
                    String newXML = DocumentToString(doc);
                    workflow.setBpmnContent(newXML);
                    bpmNfileRepository.save(workflow);
                    historyBPMNRepository.save(historyBPMN);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void updateWorkItems(String inputXML){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check all workItems - dataObjectReference in BPMN
            NodeList list = doc.getElementsByTagNameNS("*","dataObjectReference");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String elementId = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    boolean needToSave = false;
                    boolean nameChanged = false;


                    if(Pattern.matches("WorkItem_([0-9]+)_.*", elementId)) {
                        Pattern p = Pattern.compile("WorkItem_([0-9]+)_.*");
                        Matcher m = p.matcher(elementId);

                        if(m.find()){
                            long workItemId = Long.parseLong(m.group(1));
                            WorkItem workItem = workItemRepository.findById(workItemId).get();

                            if(!workItem.getName().equals(name)){ //Check if name was changed
                                workItem.setName(name);
                                needToSave = true;
                                nameChanged = true;
                            }
                            if(needToSave){
                                workItemRepository.save(workItem);
                            }
                            if(nameChanged){
                                updateWorkItemInAllWorkflows(workItem, true,  null);
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void updateWorkItemInAllWorkflows(WorkItem workItem, boolean nameChanged, Process alreadyChangedProcess) {

        var listOfProcesses = processRepository.findAll();
        boolean alreadyChanged = false;
        if(alreadyChangedProcess != null){
            alreadyChanged = true;
        }
        for (Process proc : listOfProcesses) { //Check all processes
            if(alreadyChanged){
                if (proc.getId() == alreadyChangedProcess.getId()) {
                    continue;
                }
            }
            BPMNfile workflow = proc.getWorkflow();
            if(workflow == null){
                continue;
            }
            String XMLFile = workflow.getBpmnContent();
            var historyBPMN = new HistoryBPMN();
            historyBPMN.setProcess(workflow.getProcess());
            historyBPMN.setBpmnContent(XMLFile);
            historyBPMN.setChangeDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));
                boolean saveNeeded = false;

                //Check all dataObjectReference
                NodeList list = doc.getElementsByTagNameNS("*","dataObjectReference");
                for (int temp = 0; temp < list.getLength(); temp++) {

                    Node node = list.item(temp);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String elementId = element.getAttribute("id");
                        if(Pattern.matches("WorkItem_([0-9]+)_.*", elementId)) {
                            Pattern p = Pattern.compile("WorkItem_([0-9]+)_.*");
                            Matcher m = p.matcher(elementId);

                            if (m.find()) {
                                long foundId = Long.parseLong(m.group(1));
                                if (foundId == workItem.getId()) {
                                    if (nameChanged) {
                                        element.setAttribute("name", workItem.getName());
                                        saveNeeded = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if(saveNeeded){
                    String newXML = DocumentToString(doc);
                    workflow.setBpmnContent(newXML);
                    bpmNfileRepository.save(workflow);
                    historyBPMNRepository.save(historyBPMN);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public boolean removeProcessFromAllWorkflows(Process processToDelete){
        var listOfProcesses = processToDelete.getPartOfProcess();
        for (Process proc : listOfProcesses) { //Check all processes
            BPMNfile workflow = proc.getWorkflow();
            if(workflow == null){
                continue;
            }
            String XMLFile = workflow.getBpmnContent();
            //create new history of BPMN
            var historyBPMN = new HistoryBPMN();
            historyBPMN.setProcess(workflow.getProcess());
            historyBPMN.setBpmnContent(XMLFile);
            historyBPMN.setChangeDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));


            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            boolean save = false;
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));

                //Check previous type of task
                int deletedElements = 0;
                NodeList list = doc.getElementsByTagNameNS("*","callActivity");
                for (int temp = 0; temp - deletedElements < list.getLength(); temp++) {
                    Node node = list.item(temp - deletedElements);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String callActivityId = element.getAttribute("id");
                        if(Pattern.matches("Element_([0-9]+)_.*", callActivityId)) {
                            Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                            Matcher m = p.matcher(callActivityId);

                            if (m.find()) {
                                long foundId = Long.parseLong(m.group(1));
                                if (foundId == processToDelete.getId()) {
                                    List<String> allFlowsId = new ArrayList<>();


                                    NodeList flows = element.getElementsByTagNameNS("*","incoming"); // all incoming flows
                                    for (int i = 0; i < flows.getLength(); i++) {

                                        Node flowNode = flows.item(i);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            allFlowsId.add(flowNode.getTextContent());
                                        }
                                    }
                                    flows = element.getElementsByTagNameNS("*","outgoing"); // all outgoing flows
                                    for (int i = 0; i < flows.getLength(); i++) {

                                        Node flowNode = flows.item( i);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            allFlowsId.add(flowNode.getTextContent());
                                        }
                                    }
                                    int deleteCount = 0;
                                    flows = doc.getElementsByTagNameNS("*","sequenceFlow");
                                    for (int i = 0; i - deleteCount < flows.getLength(); i++) {

                                        Node flowNode = flows.item(i - deleteCount);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element flowElement = (org.w3c.dom.Element) flowNode;
                                            String actualFlowId = flowElement.getAttribute("id");
                                            if(allFlowsId.stream().anyMatch(id -> id.equals(actualFlowId))) {
                                                Node parentNode = flowElement.getParentNode();
                                                parentNode.removeChild(flowElement);
                                                deleteCount++;
                                            }
                                        }
                                    }
                                    deleteCount = 0;
                                    flows = doc.getElementsByTagNameNS("*","BPMNEdge");
                                    for (int i = 0; i - deleteCount < flows.getLength(); i++) {
                                        Node flowNode = flows.item(i - deleteCount);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element flowElement = (org.w3c.dom.Element) flowNode;
                                            String actualFlowId = flowElement.getAttribute("bpmnElement");
                                            if(allFlowsId.stream().anyMatch(id -> id.equals(actualFlowId))) {
                                                Node parentNode = flowElement.getParentNode();
                                                parentNode.removeChild(flowElement);
                                                deleteCount++;
                                            }
                                        }
                                    }
                                    deleteCount = 0;
                                    NodeList shapes = doc.getElementsByTagNameNS("*","BPMNShape");
                                    for (int i = 0; i - deleteCount < shapes.getLength(); i++) {
                                        Node shape = shapes.item(i - deleteCount);
                                        if (shape.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element shapeElement = (org.w3c.dom.Element) shape;
                                            String shapeId = shapeElement.getAttribute("bpmnElement");
                                            if(shapeId.equals(callActivityId)) {
                                                Node parentNode = shapeElement.getParentNode();
                                                parentNode.removeChild(shapeElement);
                                                deleteCount++;
                                            }
                                        }
                                    }

                                    Node parent = element.getParentNode();
                                    parent.removeChild(element);
                                    String newXML = DocumentToString(doc);
                                    workflow.setBpmnContent(newXML);
                                    save = true;
                                    deletedElements++;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if(save){
                bpmNfileRepository.save(workflow);
                historyBPMNRepository.save(historyBPMN);
            }
        }
        return true;
    }

    public boolean removeTaskFromAllWorkflows(Task taskToDelete){
        var listOfProcesses = taskToDelete.getPartOfProcess();
        for (Process proc : listOfProcesses) { //Check all processes
            BPMNfile workflow = proc.getWorkflow();
            if(workflow == null){
                continue;
            }
            String XMLFile = workflow.getBpmnContent();
            //create new history of BPMN
            var historyBPMN = new HistoryBPMN();
            historyBPMN.setProcess(workflow.getProcess());
            historyBPMN.setBpmnContent(XMLFile);
            historyBPMN.setChangeDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            boolean save = false;
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));

                //Check previous type of task
                int deletedElements = 0;
                NodeList list = doc.getElementsByTagNameNS("*", taskToDelete.getTaskType());
                for (int temp = 0; temp - deletedElements < list.getLength(); temp++) {
                    Node node = list.item(temp - deletedElements);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String callActivityId = element.getAttribute("id");
                        if(Pattern.matches("Element_([0-9]+)_.*", callActivityId)) {
                            Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                            Matcher m = p.matcher(callActivityId);

                            if (m.find()) {
                                long foundId = Long.parseLong(m.group(1));
                                if (foundId == taskToDelete.getId()) {
                                    List<String> allFlowsId = new ArrayList<>();
                                    List<String> allInputAssociations = new ArrayList<>();
                                    List<String> allOutputAssociations = new ArrayList<>();


                                    NodeList flows = element.getElementsByTagNameNS("*","incoming"); // all incoming flows
                                    for (int i = 0; i < flows.getLength(); i++) {

                                        Node flowNode = flows.item(i);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            allFlowsId.add(flowNode.getTextContent());
                                        }
                                    }
                                    flows = element.getElementsByTagNameNS("*","outgoing"); // all outgoing flows
                                    for (int i = 0; i < flows.getLength(); i++) {

                                        Node flowNode = flows.item(i);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            allFlowsId.add(flowNode.getTextContent());
                                        }
                                    }

                                    NodeList associations = element.getElementsByTagNameNS("*","dataInputAssociation");
                                    for (int i = 0; i < associations.getLength(); i++) {

                                        Node assocNode = associations.item(i);
                                        if (assocNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element assocElement = (org.w3c.dom.Element) assocNode;
                                            allInputAssociations.add(assocElement.getAttribute("id"));
                                        }
                                    }

                                    associations = element.getElementsByTagNameNS("*","dataOutputAssociation");
                                    for (int i = 0; i < associations.getLength(); i++) {

                                        Node assocNode = associations.item(i);
                                        if (assocNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element assocElement = (org.w3c.dom.Element) assocNode;
                                            allOutputAssociations.add(assocElement.getAttribute("id"));
                                        }
                                    }

                                    int deleteCount = 0;
                                    flows = doc.getElementsByTagNameNS("*","sequenceFlow");
                                    for (int i = 0; i - deleteCount < flows.getLength(); i++) {

                                        Node flowNode = flows.item(i - deleteCount);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element flowElement = (org.w3c.dom.Element) flowNode;
                                            String actualFlowId = flowElement.getAttribute("id");
                                            if(allFlowsId.stream().anyMatch(id -> id.equals(actualFlowId))) {
                                                Node parentNode = flowElement.getParentNode();
                                                parentNode.removeChild(flowElement);
                                                deleteCount++;
                                            }
                                        }
                                    }

                                    deleteCount = 0;
                                    NodeList edges = doc.getElementsByTagNameNS("*","BPMNEdge");
                                    for (int i = 0; i - deleteCount < edges.getLength(); i++) {
                                        Node edgeNode = edges.item(i - deleteCount);
                                        if (edgeNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element edgeElement = (org.w3c.dom.Element) edgeNode;
                                            String actualId = edgeElement.getAttribute("bpmnElement");
                                            if(allFlowsId.stream().anyMatch(id -> id.equals(actualId))) {
                                                Node parentNode = edgeElement.getParentNode();
                                                parentNode.removeChild(edgeElement);
                                                deleteCount++;
                                            } else if(allInputAssociations.stream().anyMatch(id -> id.equals(actualId))) {
                                                Node parentNode = edgeElement.getParentNode();
                                                parentNode.removeChild(edgeElement);
                                                deleteCount++;
                                            } else if(allOutputAssociations.stream().anyMatch(id -> id.equals(actualId))) {
                                            Node parentNode = edgeElement.getParentNode();
                                            parentNode.removeChild(edgeElement);
                                            deleteCount++;
                                            }
                                        }
                                    }
                                    deleteCount = 0;
                                    NodeList shapes = doc.getElementsByTagNameNS("*","BPMNShape");
                                    for (int i = 0; i - deleteCount < shapes.getLength(); i++) {
                                        Node shape = shapes.item(i - deleteCount);
                                        if (shape.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element shapeElement = (org.w3c.dom.Element) shape;
                                            String shapeId = shapeElement.getAttribute("bpmnElement");
                                            if(shapeId.equals(callActivityId)) {
                                                Node parentNode = shapeElement.getParentNode();
                                                parentNode.removeChild(shapeElement);
                                                deleteCount++;
                                            }
                                        }
                                    }

                                    Node parent = element.getParentNode();
                                    parent.removeChild(element);
                                    String newXML = DocumentToString(doc);
                                    workflow.setBpmnContent(newXML);
                                    save = true;
                                    deletedElements++;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if(save){
                bpmNfileRepository.save(workflow);
                historyBPMNRepository.save(historyBPMN);
            }
        }
        return true;
    }

    public boolean removeInputConnectionFromAllWorkflows(Task task, WorkItem workItem) {
        var listOfProcesses = task.getPartOfProcess();
        for (Process proc : listOfProcesses) { //Check all processes
            BPMNfile workflow = proc.getWorkflow();
            if (workflow == null) {
                continue;
            }
            String XMLFile = workflow.getBpmnContent();
            //create new history of BPMN
            var historyBPMN = new HistoryBPMN();
            historyBPMN.setProcess(workflow.getProcess());
            historyBPMN.setBpmnContent(XMLFile);
            historyBPMN.setChangeDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            boolean save = false;
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));

                //Check previous type of task
                int deletedElements = 0;
                NodeList list = doc.getElementsByTagNameNS("*", task.getTaskType());
                for (int temp = 0; temp - deletedElements < list.getLength(); temp++) {
                    Node node = list.item(temp - deletedElements);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String callActivityId = element.getAttribute("id");
                        if (Pattern.matches("Element_([0-9]+)_.*", callActivityId)) {
                            Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                            Matcher m = p.matcher(callActivityId);

                            if (m.find()) {
                                long foundId = Long.parseLong(m.group(1));
                                if (foundId == task.getId()) {
                                    List<String> allInputAssociations = new ArrayList<>();

                                    int deleteCount = 0;
                                    NodeList associations = element.getElementsByTagNameNS("*","dataInputAssociation");
                                    for (int i = 0; i - deleteCount < associations.getLength(); i++) {
                                        Node assocNode = associations.item(i - deleteCount);
                                        if (assocNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element assocElement = (org.w3c.dom.Element) assocNode;
                                            String workId = assocElement.getElementsByTagNameNS("*","sourceRef").item(0).getTextContent();

                                            if (Pattern.matches("WorkItem_([0-9]+)_.*", workId)) {
                                                Pattern p1 = Pattern.compile("WorkItem_([0-9]+)_.*");
                                                Matcher m1 = p1.matcher(workId);

                                                if (m1.find()) {
                                                    long foundWorkItemId = Long.parseLong(m1.group(1));
                                                    if (foundWorkItemId == workItem.getId()) {
                                                        allInputAssociations.add(assocElement.getAttribute("id"));
                                                        Node parent = assocElement.getParentNode();
                                                        parent.removeChild(assocElement);
                                                        deleteCount++;
                                                        save = true;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    deleteCount = 0;
                                    NodeList edges = doc.getElementsByTagNameNS("*","BPMNEdge");
                                    for (int i = 0; i - deleteCount < edges.getLength(); i++) {
                                        Node edgeNode = edges.item(i - deleteCount);
                                        if (edgeNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element edgeElement = (org.w3c.dom.Element) edgeNode;
                                            String actualId = edgeElement.getAttribute("bpmnElement");
                                            if (allInputAssociations.stream().anyMatch(id -> id.equals(actualId))) {
                                                Node parentNode = edgeElement.getParentNode();
                                                parentNode.removeChild(edgeElement);
                                                deleteCount++;
                                                save = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (save) {
                    String newXML = DocumentToString(doc);
                    workflow.setBpmnContent(newXML);
                    bpmNfileRepository.save(workflow);
                    historyBPMNRepository.save(historyBPMN);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void removeOutputConnectionFromAllWorkflows(Task task, WorkItem workItem) {
        var listOfProcesses = task.getPartOfProcess();
        for (Process proc : listOfProcesses) { //Check all processes
            BPMNfile workflow = proc.getWorkflow();
            if (workflow == null) {
                continue;
            }
            String XMLFile = workflow.getBpmnContent();
            //create new history of BPMN
            var historyBPMN = new HistoryBPMN();
            historyBPMN.setProcess(workflow.getProcess());
            historyBPMN.setBpmnContent(XMLFile);
            historyBPMN.setChangeDate(LocalDateTime.now());

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            boolean save = false;
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));

                //Check previous type of task
                int deletedElements = 0;
                NodeList list = doc.getElementsByTagNameNS("*", task.getTaskType());
                for (int temp = 0; temp - deletedElements < list.getLength(); temp++) {
                    Node node = list.item(temp - deletedElements);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String callActivityId = element.getAttribute("id");
                        if (Pattern.matches("Element_([0-9]+)_.*", callActivityId)) {
                            Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                            Matcher m = p.matcher(callActivityId);

                            if (m.find()) {
                                long foundId = Long.parseLong(m.group(1));
                                if (foundId == task.getId()) {
                                    List<String> allOutputAssociations = new ArrayList<>();

                                    int deleteCount = 0;
                                    NodeList associations = element.getElementsByTagNameNS("*","dataOutputAssociation");
                                    for (int i = 0; i - deleteCount < associations.getLength(); i++) {
                                        Node assocNode = associations.item(i - deleteCount);
                                        if (assocNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element assocElement = (org.w3c.dom.Element) assocNode;
                                            String workId = assocElement.getElementsByTagNameNS("*","targetRef").item(0).getTextContent();

                                            if (Pattern.matches("WorkItem_([0-9]+)_.*", workId)) {
                                                Pattern p1 = Pattern.compile("WorkItem_([0-9]+)_.*");
                                                Matcher m1 = p1.matcher(workId);

                                                if (m1.find()) {
                                                    long foundWorkItemId = Long.parseLong(m1.group(1));
                                                    if (foundWorkItemId == workItem.getId()) {
                                                        allOutputAssociations.add(assocElement.getAttribute("id"));
                                                        Node parent = assocElement.getParentNode();
                                                        parent.removeChild(assocElement);
                                                        deleteCount++;
                                                        save = true;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    deleteCount = 0;
                                    NodeList edges = doc.getElementsByTagNameNS("*","BPMNEdge");
                                    for (int i = 0; i - deleteCount < edges.getLength(); i++) {
                                        Node edgeNode = edges.item(i - deleteCount);
                                        if (edgeNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element edgeElement = (org.w3c.dom.Element) edgeNode;
                                            String actualId = edgeElement.getAttribute("bpmnElement");
                                            if (allOutputAssociations.stream().anyMatch(id -> id.equals(actualId))) {
                                                Node parentNode = edgeElement.getParentNode();
                                                parentNode.removeChild(edgeElement);
                                                deleteCount++;
                                                save = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (save) {
                    String newXML = DocumentToString(doc);
                    workflow.setBpmnContent(newXML);
                    bpmNfileRepository.save(workflow);
                    historyBPMNRepository.save(historyBPMN);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public boolean removeWorkItemFromAllWorkflows(WorkItem workItemToDelete){
        var listOfProcesses = processRepository.findAll();
        for (Process proc : listOfProcesses) { //Check all processes
            BPMNfile workflow = proc.getWorkflow();
            if(workflow == null){
                continue;
            }
            boolean save = false;
            String XMLFile = workflow.getBpmnContent();
            //create new history of BPMN
            var historyBPMN = new HistoryBPMN();
            historyBPMN.setProcess(workflow.getProcess());
            historyBPMN.setBpmnContent(XMLFile);
            historyBPMN.setChangeDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));
                NodeList list = doc.getElementsByTagNameNS("*","dataObjectReference");
                int deletedNodes = 0;
                for (int temp = 0; temp - deletedNodes < list.getLength(); temp++) {
                    Node node = list.item(temp - deletedNodes);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String workItemId = element.getAttribute("id");
                        if(Pattern.matches("WorkItem_([0-9]+)_.*", workItemId)) {
                            Pattern p = Pattern.compile("WorkItem_([0-9]+)_.*");
                            Matcher m = p.matcher(workItemId);

                            if (m.find()) {
                                long foundId = Long.parseLong(m.group(1));
                                if (foundId == workItemToDelete.getId()) {
                                    String DataObjectRefId = element.getAttribute("dataObjectRef");
                                    List<String> allInputAssociations = new ArrayList<>();
                                    List<String> allOutputAssociations = new ArrayList<>();

                                    int deleteCount = 0;
                                    NodeList dataObjects = element.getElementsByTagNameNS("*","dataObject");
                                    for (int i = 0; i - deleteCount < dataObjects.getLength(); i++) {

                                        Node dataObject = dataObjects.item(i - deleteCount);
                                        if (dataObject.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element dataObjectElement = (org.w3c.dom.Element) dataObject;
                                            if (dataObjectElement.getAttribute("id").equals(DataObjectRefId)) {
                                                Node parent = dataObject.getParentNode();
                                                parent.removeChild(dataObjectElement);
                                                deleteCount++;
                                            }
                                        }
                                    }

                                    deleteCount = 0;
                                    NodeList inputAssociations = element.getElementsByTagNameNS("*","dataInputAssociation"); // all input associations
                                    for (int i = 0; i - deleteCount < inputAssociations.getLength(); i++) {
                                        Node inputAssociation = inputAssociations.item(i - deleteCount);
                                        if (inputAssociation.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element inputAssociationElement = (org.w3c.dom.Element) inputAssociation;
                                            String workId = inputAssociationElement.getElementsByTagNameNS("*","sourceRef").item(0).getTextContent();
                                            if (workId.equals(workItemId)) {
                                                allInputAssociations.add(inputAssociationElement.getAttribute("id"));
                                                Node parent = inputAssociationElement.getParentNode();
                                                parent.removeChild(inputAssociationElement);
                                                deleteCount++;
                                            }
                                        }
                                    }

                                    deleteCount = 0;
                                    NodeList outputAssociations = element.getElementsByTagNameNS("*","dataOutputAssociation"); // all input associations
                                    for (int i = 0; i - deleteCount < outputAssociations.getLength(); i++) {
                                        Node outputAssociation = outputAssociations.item(i - deleteCount);
                                        if (outputAssociation.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element outputAssociationElement = (org.w3c.dom.Element) outputAssociation;
                                            String workId = outputAssociationElement.getElementsByTagNameNS("*","targetRef").item(0).getTextContent();
                                            if (workId.equals(workItemId)) {
                                                allOutputAssociations.add(outputAssociationElement.getAttribute("id"));
                                                Node parent = outputAssociationElement.getParentNode();
                                                parent.removeChild(outputAssociationElement);
                                                deleteCount++;
                                            }
                                        }
                                    }

                                    deleteCount = 0;
                                    NodeList edges = doc.getElementsByTagNameNS("*","BPMNEdge");
                                    for (int i = 0; i - deleteCount < edges.getLength(); i++) {
                                        Node edgeNode = edges.item(i - deleteCount);
                                        if (edgeNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element edgeElement = (org.w3c.dom.Element) edgeNode;
                                            String actualEdgeId = edgeElement.getAttribute("bpmnElement");
                                            if (allInputAssociations.stream().anyMatch(id -> id.equals(actualEdgeId))) {
                                                Node parentNode = edgeElement.getParentNode();
                                                parentNode.removeChild(edgeElement);
                                                deleteCount++;
                                            } else if (allOutputAssociations.stream().anyMatch(id -> id.equals(actualEdgeId))) {
                                                Node parentNode = edgeElement.getParentNode();
                                                parentNode.removeChild(edgeElement);
                                                deleteCount++;
                                            }
                                        }
                                    }

                                    deleteCount = 0;
                                    NodeList shapes = doc.getElementsByTagNameNS("*","BPMNShape");
                                    for (int i = 0; i - deleteCount < shapes.getLength(); i++) {
                                        Node shape = shapes.item(i - deleteCount);
                                        if (shape.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element shapeElement = (org.w3c.dom.Element) shape;
                                            String shapeId = shapeElement.getAttribute("bpmnElement");
                                            if (shapeId.equals(workItemId)) {
                                                Node parentNode = shapeElement.getParentNode();
                                                parentNode.removeChild(shapeElement);
                                                deleteCount++;
                                            }
                                        }
                                    }

                                    Node parent = element.getParentNode();
                                    parent.removeChild(element);
                                    String newXML = DocumentToString(doc);
                                    workflow.setBpmnContent(newXML);
                                    deletedNodes++;
                                    save = true;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if(save) {
                bpmNfileRepository.save(workflow);
                historyBPMNRepository.save(historyBPMN);
            }
        }
        return true;
    }

    public boolean canRestoreBPMN(String xml){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(xml)));

            for(String type : bpmnElements){
                NodeList list = doc.getElementsByTagNameNS("*", type);
                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String activity = element.getAttribute("id");
                        if (Pattern.matches("Element_([0-9]+)_.*", activity)) {
                            Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                            Matcher m = p.matcher(activity);
                            if (m.find()) {
                                long foundId = Long.parseLong(m.group(1));
                                if(!elementRepository.existsById(foundId)){
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            NodeList list = doc.getElementsByTagNameNS("*","dataObjectReference");
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String workItem = element.getAttribute("id");
                    if (Pattern.matches("WorkItem_([0-9]+)_.*", workItem)) {
                        Pattern p = Pattern.compile("WorkItem_([0-9]+)_.*");
                        Matcher m = p.matcher(workItem);

                        if (m.find()) {
                            long foundId = Long.parseLong(m.group(1));
                            if(!workItemRepository.existsById(foundId)){
                                return false;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String prepareImportedFile(String xml){
        String returnXML = xml;


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(xml)));

            for(String type : bpmnElements){
                NodeList list = doc.getElementsByTagNameNS("*", type);
                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String id = element.getAttribute("id");
                        returnXML = returnXML.replaceAll(id, "Element_new_" + id);
                    }
                }
            }
            NodeList list = doc.getElementsByTagNameNS("*","dataObjectReference");
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String id = element.getAttribute("id");
                    returnXML = returnXML.replaceAll(id, "WorkItem_new_" + id);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return returnXML;
    }


    private static String DocumentToString(org.w3c.dom.Document newDoc) throws Exception{
        DOMSource domSource = new DOMSource(newDoc);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        return sw.toString();
    }
}
