package cz.sli0095.promod.controllers;

import cz.sli0095.promod.entities.Element;
import com.fasterxml.jackson.annotation.JsonView;
import cz.sli0095.promod.services.ElementService;
import cz.sli0095.promod.utils.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ElementController {

    @Autowired
    ElementService elementService;

    @JsonView(Views.Default.class)
    @GetMapping("/elements")
    public ResponseEntity<List<Element>> getElements() {
        List<Element> elements = elementService.getAllElements();
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/elements/all")
    public ResponseEntity<List<Element>> getElementsTemplates(@RequestParam Long userId) {
        List<Element> elements = elementService.getAllUserCanView(userId);
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/elements/allCanEdit")
    public ResponseEntity<List<Element>> getElementsTemplatesCanEdit(@RequestParam Long userId) {
        List<Element> elements = elementService.getAllUserCanEdit(userId);
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @JsonView(Views.Default.class)
    @GetMapping("/elements/forProcess")
    public ResponseEntity<List<Element>> getElementsTemplatesCanEdit(@RequestParam Long userId, @RequestParam Long processId, @RequestParam long projectId) {
        List<Element> elements = elementService.getUsableInProcessForUser(userId, processId, projectId);
        if(elements != null){
            return ResponseEntity.ok(elements);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
