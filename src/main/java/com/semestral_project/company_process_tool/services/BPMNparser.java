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
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
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
    DocumentRepository documentRepository;
    @Autowired
    ArtifactRepository artifactRepository;
    @Autowired
    ElementRepository elementRepository;
    @Autowired
    WorkItemRepository workItemRepository;
    @Autowired
    BPMNfileRepository bpmNfileRepository;


    private  List<Element> inXML;

    @Transactional
    public boolean saveBPMN(BPMNfile file, Process process) {
        inXML = new ArrayList<Element>();

        var bpmn_to_delete = process.getWorkflow();
        file.setProcess(process);
        String bpmnContent = file.getBpmnContent();
        bpmnContent = this.newWorkItems(bpmnContent);
        bpmnContent = this.newProcesses(bpmnContent);
        bpmnContent = this.newTasks(bpmnContent);
        file.setProcess(process);
        file.setBpmnContent(bpmnContent);
        file = bpmNfileRepository.save(file);
        process.setWorkflow(file);
        processRepository.save(process);
        if(bpmn_to_delete != null){
            bpmNfileRepository.delete(bpmn_to_delete);
        }
        this.updateProcesses(bpmnContent, process);
        this.updateTasks(bpmnContent, process);
        this.updateWorkItems(bpmnContent, process);
        return true;
    }

    //@Transactional
    private String newWorkItems(String inputXML){
        String returnXML = inputXML;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check new Artifacts and Documents
            NodeList list = doc.getElementsByTagName("bpmn:dataObjectReference");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String oldId = element.getAttribute("id");
                    String name = element.getAttribute("name");

                    if (oldId.contains("Artifact_new_")) { //Create new Artifact
                        Artifact a = new Artifact();
                        String unchangedId = oldId.substring(12); //_DataObjectReference_....
                        a.setName(name);
                        Artifact savedArtifact = artifactRepository.save(a);
                        String newId = "Document_" + savedArtifact.getId() + unchangedId;

                        returnXML = returnXML.replaceAll(oldId, newId);

                    } if (oldId.contains("Document_new_")) { //Create new Document
                        com.semestral_project.company_process_tool.entities.Document d = new com.semestral_project.company_process_tool.entities.Document();
                        String unchangedId = oldId.substring(12); //_DataObjectReference_....
                        d.setName(name);
                        com.semestral_project.company_process_tool.entities.Document savedDocument = documentRepository.save(d);
                        String newId = "Document_" + savedDocument.getId() + unchangedId;

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
    private String newProcesses(String inputXML){
        String returnXML = inputXML;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check new Processes
            NodeList list = doc.getElementsByTagName("bpmn:callActivity");
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

    private String createNewTask(Document doc, String returnXML, String type){
        NodeList list = doc.getElementsByTagName("bpmn:" + type);
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
                    Task savedTask = taskRepository.save(t);
                    String newId = "Element_" + savedTask.getId() + unchangedId;

                    returnXML = returnXML.replaceAll(oldId, newId);
                }
            }
        }
        return returnXML;
    }

    //@Transactional
    private String newTasks(String inputXML){
        String returnXML = inputXML;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            returnXML = createNewTask(doc, returnXML, "task");
            returnXML = createNewTask(doc, returnXML, "sendTask");
            returnXML = createNewTask(doc, returnXML, "receiveTask");
            returnXML = createNewTask(doc, returnXML, "userTask");
            returnXML = createNewTask(doc, returnXML, "manualTask");
            returnXML = createNewTask(doc, returnXML, "serviceTask");
            returnXML = createNewTask(doc, returnXML, "scriptTask");
            returnXML = createNewTask(doc, returnXML, "businessRuleTask");
            //returnXML = createNewTask(doc, returnXML, "subProcess");

            return returnXML;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return returnXML;
        }
    }

    //@Transactional
    private String updateProcesses(String inputXML, Process process){
        String returnXML = inputXML;
        //inXML = new ArrayList<Element>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check all Processes - callActivities in BPMN
            NodeList list = doc.getElementsByTagName("bpmn:callActivity");
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
                            var isPartOf = process1.getPartOfProcess();
                            if(isPartOf == null){
                                isPartOf = new ArrayList<>();
                            }
                            if(!isPartOf.contains(process)){ //Check if is sub process already part of Process
                                isPartOf.add(process);
                                process1.setPartOfProcess(isPartOf);
                                needToSave = true;
                            }
                            if(needToSave){
                                elementRepository.save(process1);
                            }
                            if(nameChanged == true){
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
            return returnXML;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return returnXML;
        }
    }

    public boolean updateProcessInAllWorkflows(Process process, boolean nameChanged, Process alreadyChangedProcess) {

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
            BPMNfile workflow = proc.getWorkflow();
            if(workflow == null){
                continue;
            }
            String XMLFile = workflow.getBpmnContent();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //dbf.setNamespaceAware(false);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));

                //Check previous type of task
                NodeList list = doc.getElementsByTagName("bpmn:callActivity");
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
                                        String newXML = DocumentToString(doc);
                                        workflow.setBpmnContent(newXML);
                                        bpmNfileRepository.save(workflow);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void makeTaskUpdate(org.w3c.dom.Document doc, Process process, String type){
        NodeList list = doc.getElementsByTagName("bpmn:" + type);
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
                        var isPartOf = task1.getPartOfProcess();
                        if(isPartOf == null){
                            isPartOf = new ArrayList<>();
                        }
                        if(!isPartOf.contains(process)){ //Check if task already part of Process
                            isPartOf.add(process);
                            task1.setPartOfProcess(isPartOf);
                            needToSave = true;
                        }

                        //Check added Inputs
                        NodeList listOfInputs = element.getElementsByTagName("bpmn:dataInputAssociation");
                        for (int temp2 = 0; temp2 < listOfInputs.getLength(); temp2++) {

                            Node node2 = listOfInputs.item(temp2);

                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                org.w3c.dom.Element elementInput = (org.w3c.dom.Element) node2;
                                if(elementInput.getElementsByTagName("bpmn:sourceRef").item(0) == null){
                                    continue;
                                }
                                String inputId = elementInput.getElementsByTagName("bpmn:sourceRef").item(0).getTextContent();
                                if (Pattern.matches("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*", inputId)) {
                                    Pattern p1 = Pattern.compile("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*");
                                    Matcher m1 = p1.matcher(inputId);

                                    if (m1.find()) {
                                        long workItemId;
                                        if(m1.group(1) != null){
                                            workItemId = Long.parseLong(m1.group(1));
                                        } else {
                                            workItemId = Long.parseLong(m1.group(2));
                                        }
                                        WorkItem workItem = workItemRepository.findById(workItemId).get();

                                        List<WorkItem> inputList = task1.getMandatoryInputs();
                                        if(inputList == null){
                                            inputList = new ArrayList<>();
                                        }
                                        if (!inputList.contains(workItem)) {
                                            List<Task> tasksList = workItem.getAsMandatoryInput();
                                            if(tasksList == null){
                                                tasksList = new ArrayList<>();
                                            }
                                            tasksList.add(task1);
                                            workItem.setAsMandatoryInput(tasksList);
                                            workItemRepository.save(workItem);
                                        }

                                    }
                                }
                            }
                        }

                        //Check added Outputs
                        NodeList listOfOutputs = element.getElementsByTagName("bpmn:dataOutputAssociation");
                        for (int temp2 = 0; temp2 < listOfOutputs.getLength(); temp2++) {

                            Node node2 = listOfOutputs.item(temp2);

                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                org.w3c.dom.Element elementOutput = (org.w3c.dom.Element) node2;
                                String outputId = elementOutput.getElementsByTagName("bpmn:targetRef").item(0).getTextContent();

                                if (Pattern.matches("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*", outputId)) {
                                    Pattern p1 = Pattern.compile("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*");
                                    Matcher m1 = p1.matcher(outputId);

                                    if (m1.find()) {
                                        long workItemId;
                                        if(m1.group(1) != null){
                                            workItemId = Long.parseLong(m1.group(1));
                                        } else {
                                            workItemId = Long.parseLong(m1.group(2));
                                        }
                                        WorkItem workItem = workItemRepository.findById(workItemId).get();

                                        List<WorkItem> outputList = task1.getOutputs();
                                        if(outputList == null){
                                            outputList = new ArrayList<>();
                                        }
                                        if (!outputList.contains(workItem)) {
                                            List<Task> tasksList = workItem.getAsOutput();
                                            if(tasksList == null){
                                                tasksList = new ArrayList<>();
                                            }
                                            tasksList.add(task1);
                                            workItem.setAsOutput(tasksList);
                                            workItemRepository.save(workItem);
                                        }
                                    }
                                }
                            }
                        }
                        if(needToSave){
                            Task task = taskRepository.save(task1);
                            this.updateTaskInAllWorkflows(task1, nameChanged,typeChanged,oldTaskType, process);
                        }
                        inXML.add(task1);
                    }
                }
            }
        }
    }

    //@Transactional
    private String updateTasks(String inputXML, Process process){
        String returnXML = inputXML;
        //inXML = new ArrayList<Element>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
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
            return returnXML;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return returnXML;
        }
    }

    public boolean updateTaskInAllWorkflows(Task task, boolean nameChanged, boolean typeChanged, String oldTaskType, Process alreadyChangedProcess) {

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

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //dbf.setNamespaceAware(false);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));
                String tagName = task.getTaskType();

                if (typeChanged) {
                    tagName = oldTaskType;
                }

                //Check previous type of task
                NodeList list = doc.getElementsByTagName("bpmn:" + tagName);
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
                                    }
                                    if (typeChanged) {
                                        //System.out.println(node.getNamespaceURI());

                                        doc.renameNode(node,"http://www.omg.org/spec/BPMN/20100524/MODEL", "bpmn:" + task.getTaskType());
                                    }
                                    String newXML = DocumentToString(doc);
                                    workflow.setBpmnContent(newXML);
                                    bpmNfileRepository.save(workflow);
                                }
                            }
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String updateWorkItems(String inputXML, Process process){
        String returnXML = inputXML;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check all workItems - dataObjectReference in BPMN
            NodeList list = doc.getElementsByTagName("bpmn:dataObjectReference");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String elementId = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    boolean needToSave = false;
                    boolean nameChanged = false;


                    if(Pattern.matches("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*", elementId)) {
                        Pattern p = Pattern.compile("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*");
                        Matcher m = p.matcher(elementId);

                        if(m.find()){
                            long workItemId;
                            if(m.group(1) != null){
                                workItemId = Long.parseLong(m.group(1));
                            } else {
                                workItemId = Long.parseLong(m.group(2));
                            }
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
            return returnXML;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return returnXML;
        }
    }

    public boolean updateWorkItemInAllWorkflows(WorkItem workItem, boolean nameChanged, Process alreadyChangedProcess) {

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

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //dbf.setNamespaceAware(false);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));

                //Check previous type of task
                NodeList list = doc.getElementsByTagName("bpmn:dataObjectReference");
                boolean save = false;
                for (int temp = 0; temp < list.getLength(); temp++) {

                    Node node = list.item(temp);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String elementId = element.getAttribute("id");
                        if(Pattern.matches("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*", elementId)) {
                            Pattern p = Pattern.compile("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*");
                            Matcher m = p.matcher(elementId);

                            if (m.find()) {
                                long foundId;
                                if(m.group(1) != null){
                                    foundId = Long.parseLong(m.group(1));
                                } else {
                                    foundId = Long.parseLong(m.group(2));
                                }
                                if (foundId == workItem.getId()) {
                                    if (nameChanged) {
                                        element.setAttribute("name", workItem.getName());
                                        String newXML = DocumentToString(doc);
                                        workflow.setBpmnContent(newXML);
                                        bpmNfileRepository.save(workflow);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean removeProcessFromAllWorkflows(Process processToDelete){
        var listOfProcesses = processToDelete.getPartOfProcess();
        for (Process proc : listOfProcesses) { //Check all processes
            BPMNfile workflow = proc.getWorkflow();
            if(workflow == null){
                continue;
            }
            String XMLFile = workflow.getBpmnContent();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));

                //Check previous type of task
                NodeList list = doc.getElementsByTagName("bpmn:callActivity");
                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);
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
                                    NodeList flows = element.getElementsByTagName("bpmn:incoming"); // all incoming flows
                                    for (int i = 0; i < flows.getLength(); i++) {

                                        Node flowNode = flows.item(i);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element flowElement = (org.w3c.dom.Element) flowNode;
                                            allFlowsId.add(flowNode.getTextContent());
                                        }
                                    }
                                    flows = element.getElementsByTagName("bpmn:outgoing"); // all outgoing flows
                                    for (int i = 0; i < flows.getLength(); i++) {

                                        Node flowNode = flows.item(i);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element flowElement = (org.w3c.dom.Element) flowNode;
                                            allFlowsId.add(flowNode.getTextContent());
                                        }
                                    }
                                    flows = doc.getElementsByTagName("bpmn:sequenceFlow");
                                    for (int i = 0; i < flows.getLength(); i++) {

                                        Node flowNode = flows.item(i);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element flowElement = (org.w3c.dom.Element) flowNode;
                                            String actualFlowId = flowElement.getAttribute("id");
                                            if(allFlowsId.stream().anyMatch(id -> id.equals(actualFlowId))) {
                                                Node parentNode = flowElement.getParentNode();
                                                parentNode.removeChild(flowElement);
                                            }
                                        }
                                    }
                                    flows = doc.getElementsByTagName("bpmndi:BPMNEdge");
                                    for (int i = 0; i < flows.getLength(); i++) {
                                        Node flowNode = flows.item(i);
                                        if (flowNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element flowElement = (org.w3c.dom.Element) flowNode;
                                            String actualFlowId = flowElement.getAttribute("bpmnElement");
                                            if(allFlowsId.stream().anyMatch(id -> id.equals(actualFlowId))) {
                                                Node parentNode = flowElement.getParentNode();
                                                parentNode.removeChild(flowElement);
                                            }
                                        }
                                    }

                                    NodeList shapes = doc.getElementsByTagName("bpmndi:BPMNShape");
                                    for (int i = 0; i < shapes.getLength(); i++) {
                                        Node shape = shapes.item(i);
                                        if (shape.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element shapeElement = (org.w3c.dom.Element) shape;
                                            String shapeId = shapeElement.getAttribute("bpmnElement");
                                            if(shapeId.equals(callActivityId)) {
                                                Node parentNode = shapeElement.getParentNode();
                                                parentNode.removeChild(shapeElement);
                                            }
                                        }
                                    }

                                    Node parent = element.getParentNode();
                                    parent.removeChild(element);
                                    String newXML = DocumentToString(doc);
                                    workflow.setBpmnContent(newXML);
                                    bpmNfileRepository.save(workflow);
                                }
                            }
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
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
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(XMLFile)));
                NodeList list = doc.getElementsByTagName("bpmn:dataObjectReference");
                System.out.println(list.getLength());
                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                        String workItemId = element.getAttribute("id");
                        if(Pattern.matches("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*", workItemId)) {
                            Pattern p = Pattern.compile("Artifact_([0-9]+)_.*|Document_([0-9]+)_.*");
                            Matcher m = p.matcher(workItemId);

                            if (m.find()) {
                                long foundId;
                                if(m.group(1) != null){
                                    foundId = Long.parseLong(m.group(1));
                                } else {
                                    foundId = Long.parseLong(m.group(2));
                                }
                                if (foundId == workItemToDelete.getId()) {
                                    String DataObjectRefId = element.getAttribute("dataObjectRef");
                                    List<String> allInputAssociations = new ArrayList<>();
                                    List<String> allOutputAssociations = new ArrayList<>();

                                    NodeList dataObjects = element.getElementsByTagName("bpmn:dataObject");
                                    for (int i = 0; i < dataObjects.getLength(); i++) {

                                        Node dataObject = dataObjects.item(i);
                                        if (dataObject.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element dataObjectElement = (org.w3c.dom.Element) dataObject;
                                            if (dataObjectElement.getAttribute("id").equals(DataObjectRefId)) {
                                                Node parent = dataObject.getParentNode();
                                                parent.removeChild(dataObjectElement);
                                            }
                                        }
                                    }

                                    NodeList inputAssociations = element.getElementsByTagName("bpmn:dataInputAssociation"); // all input associations
                                    for (int i = 0; i < inputAssociations.getLength(); i++) {
                                        Node inputAssociation = inputAssociations.item(i);
                                        if (inputAssociation.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element inputAssociationElement = (org.w3c.dom.Element) inputAssociation;
                                            String workId = inputAssociationElement.getElementsByTagName("bpmn:sourceRef").item(0).getTextContent();
                                            if (workId.equals(workItemId)) {
                                                allInputAssociations.add(inputAssociationElement.getAttribute("id"));
                                                Node parent = inputAssociationElement.getParentNode();
                                                parent.removeChild(inputAssociationElement);
                                            }
                                        }
                                    }

                                    NodeList outputAssociations = element.getElementsByTagName("bpmn:dataOutputAssociation"); // all input associations
                                    for (int i = 0; i < outputAssociations.getLength(); i++) {
                                        Node outputAssociation = outputAssociations.item(i);
                                        if (outputAssociation.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element outputAssociationElement = (org.w3c.dom.Element) outputAssociation;
                                            String workId = outputAssociationElement.getElementsByTagName("bpmn:targetRef").item(0).getTextContent();
                                            if (workId.equals(workItemId)) {
                                                allOutputAssociations.add(outputAssociationElement.getAttribute("id"));
                                                Node parent = outputAssociationElement.getParentNode();
                                                parent.removeChild(outputAssociationElement);
                                            }
                                        }
                                    }

                                    NodeList edges = doc.getElementsByTagName("bpmndi:BPMNEdge");
                                    for (int i = 0; i < edges.getLength(); i++) {
                                        Node edgeNode = edges.item(i);
                                        if (edgeNode.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element edgeElement = (org.w3c.dom.Element) edgeNode;
                                            String actualEdgeId = edgeElement.getAttribute("bpmnElement");
                                            if (allInputAssociations.stream().anyMatch(id -> id.equals(actualEdgeId))) {
                                                Node parentNode = edgeElement.getParentNode();
                                                parentNode.removeChild(edgeElement);
                                            } else if (allOutputAssociations.stream().anyMatch(id -> id.equals(actualEdgeId))) {
                                                Node parentNode = edgeElement.getParentNode();
                                                parentNode.removeChild(edgeElement);
                                            }
                                        }
                                    }

                                    NodeList shapes = doc.getElementsByTagName("bpmndi:BPMNShape");
                                    for (int i = 0; i < shapes.getLength(); i++) {
                                        Node shape = shapes.item(i);
                                        if (shape.getNodeType() == Node.ELEMENT_NODE) {
                                            org.w3c.dom.Element shapeElement = (org.w3c.dom.Element) shape;
                                            String shapeId = shapeElement.getAttribute("bpmnElement");
                                            if (shapeId.equals(workItemId)) {
                                                Node parentNode = shapeElement.getParentNode();
                                                parentNode.removeChild(shapeElement);
                                            }
                                        }
                                    }

                                    Node parent = element.getParentNode();
                                    parent.removeChild(element);
                                    String newXML = DocumentToString(doc);
                                    workflow.setBpmnContent(newXML);
                                    save = true;
                                }
                            }
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if(save) {
                bpmNfileRepository.save(workflow);
            }
        }
        return true;
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
