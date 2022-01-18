package com.semestral_project.company_process_tool.controllers;

import com.semestral_project.company_process_tool.entities.*;
import com.semestral_project.company_process_tool.repositories.ArtifactRelationRepository;
import com.semestral_project.company_process_tool.repositories.ArtifactRepository;
import com.semestral_project.company_process_tool.repositories.StateRepository;
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
    ArtifactRepository artifactRepository;

    @Autowired
    ArtifactRelationRepository artifactRelationRepository;

    @Autowired
    StateRepository stateRepository;

    @GetMapping("/artifacts")
    public ResponseEntity<List<Artifact>> getArtifacts() {
        try {
            return ResponseEntity.ok((List<Artifact>) artifactRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(e.getMessage()).body(null);
        }
    }

    @GetMapping("/artifacts/{id}")
    public ResponseEntity<Artifact> artifactById(@PathVariable Long id) {
        Optional<Artifact> artifactData = artifactRepository.findById(id);

        if(artifactData.isPresent()) {
            return ResponseEntity.ok(artifactData.get());
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/artifacts")
    public ResponseEntity<ResponseMessage> addArtifact(@RequestBody Artifact artifact){
        try {
            artifactRepository.save(artifact);
            return ResponseEntity.ok(new ResponseMessage("Artifact added"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/artifacts/{id}")
    public ResponseEntity<ResponseMessage> removeArtifact(@PathVariable Long id) {
        try {
            artifactRepository.deleteById(id);
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is deleted"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/artifacts/{id}")
    public ResponseEntity<ResponseMessage> updateArtifact(@PathVariable Long id, @RequestBody Artifact artifact) {
        Optional<Artifact> artifactData = artifactRepository.findById(id);

        if(artifactData.isPresent()){
            Artifact artifact_ = artifactData.get();
            artifact_ = fillArtifact(artifact_, artifact);

            artifactRepository.save(artifact_);
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not exist"));
        }
    }

    @PutMapping("/artifacts/{id}/setState")
    public ResponseEntity<ResponseMessage> setState(@PathVariable Long id, @RequestBody State state){
        Optional<Artifact> artifactData = artifactRepository.findById(id);

        if(artifactData.isPresent()){
            Artifact artifact_ = artifactData.get();
            if(state.getId() == -1 && artifact_.getArtifactState() != null)
            {
                State state_ = artifact_.getArtifactState();
                List<Artifact> artifactList = state_.getArtifacts();
                artifactList.remove(artifact_);
                state_.setArtifacts(artifactList);
                stateRepository.save(state_);
                artifact_.setArtifactState(null);
            } else if (state.getId() == -1 && artifact_.getArtifactState() == null){
                return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not have state."));
            }
            else {
                artifact_.setArtifactState(state);
            }
            artifactRepository.save(artifact_);
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not exist"));
        }
    }

    @Transactional
    @PutMapping("/artifacts/{id}/addRelation")
    public ResponseEntity<ResponseMessage> addRelation(@PathVariable Long id, @RequestBody Artifact artifact, @RequestParam String relationType) {
        if(id == artifact.getId())
            return ResponseEntity.badRequest().body(new ResponseMessage("Can not add relation to itself"));
        Optional<Artifact> artifactData = artifactRepository.findById(id);

        if(artifactData.isPresent()){
            Artifact artifact_ = artifactData.get();

            List<ArtifactRelation> relations = artifact_.getRelationsToAnotherArtifacts();
            for(ArtifactRelation relation : relations){
                if(relation.getBaseArtifact().getId() == artifact.getId()){
                    return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " already has relation to artifact " + artifact.getId()));
                }
            }
            ArtifactRelation relation = new ArtifactRelation();
            relation.setBaseArtifact(artifact);
            relation.setRelatedArtifact(artifact_);
            relation.setRelationType(relationType);
            relation = artifactRelationRepository.save(relation);
            relations.add(relation);
            artifact_.setRelationsToAnotherArtifacts(relations);
            artifactRepository.save(artifact_);
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not exist"));
        }
    }


    @Transactional
    @PutMapping("/artifacts/{id}/removeRelation")
    public ResponseEntity<ResponseMessage> removeRelation(@PathVariable Long id, @RequestBody ArtifactRelation artifactRelation) {
        Optional<Artifact> artifactData = artifactRepository.findById(id);

        if(artifactData.isPresent()){
            Artifact artifact_ = artifactData.get();
            ArtifactRelation artifactRelation_ = artifactRelationRepository.findById(artifactRelation.getId()).get();

            List<ArtifactRelation> relations = artifact_.getRelationsToAnotherArtifacts();
            relations.remove(artifactRelation_);
            artifact_.setRelationsToAnotherArtifacts(relations);
            artifactRepository.save(artifact_);
            artifactRelationRepository.delete(artifactRelation_);
            return ResponseEntity.ok(new ResponseMessage("Artifact id: " + id + " is updated"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new ResponseMessage("Artifact id: " + id + " does not exist"));
        }
    }

    private Artifact fillArtifact(Artifact oldArtifact, Artifact updatedArtifact){
        oldArtifact.setName(updatedArtifact.getName());
        oldArtifact.setBriefDescription(updatedArtifact.getBriefDescription());
        oldArtifact.setMainDescription(updatedArtifact.getMainDescription());
        oldArtifact.setVersion(updatedArtifact.getVersion());
        oldArtifact.setChangeDate(updatedArtifact.getChangeDate());
        oldArtifact.setChangeDescription(updatedArtifact.getChangeDescription());
        oldArtifact.setPurpose(updatedArtifact.getPurpose());
        oldArtifact.setKeyConsiderations(updatedArtifact.getKeyConsiderations());
        oldArtifact.setBriefOutline(updatedArtifact.getBriefOutline());
        oldArtifact.setNotation(updatedArtifact.getNotation());
        oldArtifact.setImpactOfNotHaving(updatedArtifact.getImpactOfNotHaving());
        oldArtifact.setReasonForNotNeeding(updatedArtifact.getReasonForNotNeeding());
        return oldArtifact;
    }
}
