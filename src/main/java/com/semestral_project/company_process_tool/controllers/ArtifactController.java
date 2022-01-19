package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.repositories.ArtifactRelationRepository;
import com.semestral_project.company_process_tool.repositories.ArtifactRepository;
import com.semestral_project.company_process_tool.repositories.StateRepository;
import com.semestral_project.company_process_tool.services.ArtifactService;
import com.semestral_project.company_process_tool.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ArtifactController {

    @Autowired
    ArtifactService artifactService;

    @GetMapping("/artifacts")
    public ResponseEntity<List<Artifact>> getArtifacts() {
        List<Artifact> artifacts = artifactService.getAllArtifacts();
        if(artifacts != null){
            return ResponseEntity.ok(artifacts);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/artifacts/{id}")
    public ResponseEntity<Artifact> artifactById(@PathVariable Long id) {
        Artifact artifact = artifactService.getArtifactById(id);
        if(artifact != null) {
            return ResponseEntity.ok(artifact);
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/artifacts")
    public ResponseEntity<ResponseMessage> addArtifact(@RequestBody Artifact artifact){
        if(artifactService.addArtifact(artifact)){
            return ResponseEntity.ok(new ResponseMessage("Artifact added"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact could not be added"));
        }
    }

    @DeleteMapping("/artifacts/{id}")
    public ResponseEntity<ResponseMessage> removeArtifact(@PathVariable Long id) {
        if(artifactService.deleteArtifact(id)) {
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is deleted"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " could not be deleted"));
        }
    }

    @PutMapping("/artifacts/{id}")
    public ResponseEntity<ResponseMessage> updateArtifact(@PathVariable Long id, @RequestBody Artifact artifact) {

        int status = artifactService.updateArtifact(id, artifact);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not exist"));
        }
    }

    @PutMapping("/artifacts/{id}/setState")
    public ResponseEntity<ResponseMessage> setState(@PathVariable Long id, @RequestBody State state){

        int status = artifactService.setArtifactState(id, state);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is updated"));
        } else if(status == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not exist"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not have state."));
        }
    }


    @PutMapping("/artifacts/{id}/addRelation")
    public ResponseEntity<ResponseMessage> addRelation(@PathVariable Long id, @RequestBody Artifact artifact, @RequestParam String relationType) {

        int status = artifactService.addRelationToArtifact(id, artifact, relationType);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is updated"));
        } else if(status == 2){
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not exist"));
        } else if(status == 3){
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " already has relation to artifact " + artifact.getId()));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Can not add relation to itself"));
        }
    }

    @PutMapping("/artifacts/{id}/removeRelation")
    public ResponseEntity<ResponseMessage> removeRelation(@PathVariable Long id, @RequestBody ArtifactRelation artifactRelation) {

        int status = artifactService.removeRelationFromArtifact(id, artifactRelation);
        if(status == 1){
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is updated"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not exist"));
        }
    }
}
