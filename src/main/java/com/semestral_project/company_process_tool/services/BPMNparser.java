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
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
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


    private String file;

    public String saveBPMN(String file, Process process) {

        String returnFile = file;
        returnFile = this.newWorkItems(returnFile);
        returnFile = this.newProcesses(returnFile);
        returnFile = this.newTasks(returnFile);
        returnFile = this.updateProcesses(returnFile, process);
        returnFile = this.updateTasks(returnFile, process);
        System.out.println(returnFile);
        return returnFile;
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
            NodeList list = doc.getElementsByTagName("bpmn:subProcess");
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

    //@Transactional
    private String newTasks(String inputXML){
        String returnXML = inputXML;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check new Tasks
            NodeList list = doc.getElementsByTagName("bpmn:task");
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
                        Task savedTask = taskRepository.save(t);
                        String newId = "Element_" + savedTask.getId() + unchangedId;

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
    private String updateProcesses(String inputXML, Process process){
        String returnXML = inputXML;
        List<Element> inXML = new ArrayList<Element>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check all Processes
            NodeList list = doc.getElementsByTagName("bpmn:subProcess");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String elementId = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    boolean needToSave = false;

                    if(Pattern.matches("Element_\\d+_.*", elementId)) {
                        Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                        Matcher m = p.matcher(elementId);

                        if(m.find()){
                            long process_id = Long.parseLong(m.group(1));
                            Process process1 = processRepository.findById(process_id).get();

                            if(process1.getName() != name){ //Check if name was changed
                                process1.setName(name);
                                needToSave = true;
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
                                processRepository.save(process1);
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

    //@Transactional
    private String updateTasks(String inputXML, Process process){
        String returnXML = inputXML;
        List<Element> inXML = new ArrayList<Element>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(inputXML)));

            //Check all Tasks
            NodeList list = doc.getElementsByTagName("bpmn:task");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    String elementId = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    boolean needToSave = false;

                    if(Pattern.matches("Element_\\d+_.*", elementId)) {
                        Pattern p = Pattern.compile("Element_([0-9]+)_.*");
                        Matcher m = p.matcher(elementId);

                        if(m.find()){
                            long taskId = Long.parseLong(m.group(1));
                            Task task1 = taskRepository.findById(taskId).get();

                            if(task1.getName() != name){ //Check if name was changed
                                task1.setName(name);
                                needToSave = true;
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
                                    String inputId = elementInput.getElementsByTagName("bpmn:sourceRef").item(0).getTextContent();

                                    if (Pattern.matches("Artifact_|Document_\\d+_.*", inputId)) {
                                        Pattern p1 = Pattern.compile("Artifact_|Document_([0-9]+)_.*");
                                        Matcher m1 = p1.matcher(inputId);

                                        if (m1.find()) {
                                            long workItemId = Long.parseLong(m1.group(1));
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

                                    if (Pattern.matches("Artifact_|Document_\\d+_.*", outputId)) {
                                        Pattern p1 = Pattern.compile("Artifact_|Document_([0-9]+)_.*");
                                        Matcher m1 = p1.matcher(outputId);

                                        if (m1.find()) {
                                            long workItemId = Long.parseLong(m1.group(1));
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
                                taskRepository.save(task1);
                            }
                            inXML.add(task1);
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

    public int updateTaskInAllWorkflows() {

        return 1;
    }

    public int updateProcessInAllWorkflows() {

        return 1;
    }

    public int updateDocumentInAllWorkflows(){
        return 1;
    }

    public int updateArtifactInAllWorkflows(){
        return 1;
    }
}
