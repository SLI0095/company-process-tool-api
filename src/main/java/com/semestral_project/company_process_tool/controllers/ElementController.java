package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.Element;
import com.semestral_project.company_process_tool.services.ElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Element>> getElementsTemplates(@RequestParam Long userId) {
        List<Element> elements = elementService.getAllTemplatesForUser(userId);
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/elements/templatesCanEdit")
    public ResponseEntity<List<Element>> getElementsTemplatesCanEdit(@RequestParam Long userId) {
        List<Element> elements = elementService.getAllTemplatesForUserCanEdit(userId);
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
