package com.semestral_project.company_process_tool.services.snaphsots;

import com.semestral_project.company_process_tool.entities.Role;
import com.semestral_project.company_process_tool.entities.snapshots.SnapshotRole;
import com.semestral_project.company_process_tool.repositories.RoleRepository;
import com.semestral_project.company_process_tool.repositories.snapshots.SnapshotRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SnapshotRoleService {
    @Autowired
    SnapshotRoleRepository snapshotRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    public SnapshotRole createSnapshotRole(Role original, String snapshotDescription, SnapshotsHelper helper){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        SnapshotRole snapshot = new SnapshotRole();
        snapshot.setName(original.getName());
        snapshot.setBriefDescription(original.getBriefDescription());
        snapshot.setMainDescription(original.getMainDescription());
        snapshot.setAssignmentApproaches(original.getAssignmentApproaches());
        snapshot.setSkills(original.getSkills());
        snapshot.setChangeDate(original.getChangeDate());
        snapshot.setChangeDescription(original.getChangeDescription());
        snapshot.setVersion(original.getVersion());

        snapshot.setSnapshotDescription(snapshotDescription);
        snapshot.setSnapshotDate(LocalDate.now());
        snapshot.setOriginalRole(original);
        snapshot.setOriginalId(original.getId());

        snapshot =  snapshotRoleRepository.save(snapshot);
        helper.addRole(original.getId(), snapshot);
        return snapshot;
    }

    public Role restoreRoleFromSnapshot(SnapshotRole snapshot, SnapshotsHelper helper){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        Role role = new Role();
        role.setName(snapshot.getName());
        role.setBriefDescription(snapshot.getBriefDescription());
        role.setMainDescription(snapshot.getMainDescription());
        role.setAssignmentApproaches(snapshot.getAssignmentApproaches());
        role.setSkills(snapshot.getSkills());
        role.setChangeDate(snapshot.getChangeDate());
        role.setChangeDescription(snapshot.getChangeDescription());
        role.setVersion(snapshot.getVersion());

        role = roleRepository.save(role);
        helper.addRole(snapshot.getId(), role);
        return role;
    }

}
