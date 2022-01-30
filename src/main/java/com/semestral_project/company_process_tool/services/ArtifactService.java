package com.semestral_project.company_process_tool.services;

import com.semestral_project.company_process_tool.entities.Artifact;
import com.semestral_project.company_process_tool.entities.ArtifactRelation;
import com.semestral_project.company_process_tool.entities.State;
import com.semestral_project.company_process_tool.repositories.ArtifactRelationRepository;
import com.semestral_project.company_process_tool.repositories.ArtifactRepository;
import com.semestral_project.company_process_tool.repositories.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ArtifactService {

    @Autowired
    ArtifactRepository artifactRepository;
    @Autowired
    ArtifactRelationRepository artifactRelationRepository;
    @Autowired
    StateRepository stateRepository;
    @Autowired
    BPMNparser bpmNparser;

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

    public List<Artifact> getAllArtifacts(){
        try {
            List<Artifact> artifacts = (List<Artifact>) artifactRepository.findAll();
            return artifacts;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Artifact getArtifactById(long id){
        Optional<Artifact> artifactData = artifactRepository.findById(id);
        if(artifactData.isPresent()) {
            return artifactData.get();
        } else return null;
    }

    public boolean addArtifact(Artifact artifact){
        try {
            artifactRepository.save(artifact);
            return true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteArtifact(long id){
        try {
            if(bpmNparser.removeWorkItemFromAllWorkflows(artifactRepository.findById(id).get()))
            {
                artifactRepository.deleteById(id);
            }
            return true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int updateArtifact(long id, Artifact artifact) {
        Optional<Artifact> artifactData = artifactRepository.findById(id);

        if (artifactData.isPresent()) {
            Artifact artifact_ = artifactData.get();
            String oldName = artifact_.getName();
            artifact_ = fillArtifact(artifact_, artifact);
            artifactRepository.save(artifact_);
            bpmNparser.updateWorkItemInAllWorkflows(artifact_,true,null);
            return 1;
        } else return 2;
    }

    public int setArtifactState(long artifactId, State state){
        Optional<Artifact> artifactData = artifactRepository.findById(artifactId);
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
                return 3;
            }
            else {
                artifact_.setArtifactState(state);
            }
            artifactRepository.save(artifact_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

    //@Transactional
    public int addRelationToArtifact(long id, Artifact artifact, String relationType){
        if(id == artifact.getId())
            return 4;
        Optional<Artifact> artifactData = artifactRepository.findById(id);

        if(artifactData.isPresent()){
            Artifact artifact_ = artifactData.get();

            List<ArtifactRelation> relations = artifact_.getRelationsToAnotherArtifacts();
            for(ArtifactRelation relation : relations){
                if(relation.getBaseArtifact().getId() == artifact.getId()){
                    return 3;
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
            return 1;
        }
        else
        {
            return 2;
        }
    }
    @Transactional
    public int removeRelationFromArtifact(long id, ArtifactRelation artifactRelation){
        Optional<Artifact> artifactData = artifactRepository.findById(id);

        if(artifactData.isPresent()){
            Artifact artifact_ = artifactData.get();
            ArtifactRelation artifactRelation_ = artifactRelationRepository.findById(artifactRelation.getId()).get();

            List<ArtifactRelation> relations = artifact_.getRelationsToAnotherArtifacts();
            relations.remove(artifactRelation_);
            artifact_.setRelationsToAnotherArtifacts(relations);
            artifactRepository.save(artifact_);
            artifactRelationRepository.delete(artifactRelation_);
            return 1;
        }
        else
        {
            return 2;
        }
    }

}
