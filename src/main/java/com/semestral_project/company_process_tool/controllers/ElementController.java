package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.services.ElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ElementController {

    @Autowired
    ElementService elementService;

    @GetMapping("/elements")
    public ResponseEntity<List<Element>> getElements() {
        List<Element> elements = elementService.getAllElements();
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/elements/templates")
    public ResponseEntity<List<Element>> getElementsTemplates() {
        List<Element> elements = elementService.getAllTemplates();
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
