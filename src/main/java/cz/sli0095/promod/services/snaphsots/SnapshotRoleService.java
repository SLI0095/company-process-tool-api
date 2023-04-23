package cz.sli0095.promod.services.snaphsots;

import cz.sli0095.promod.entities.Role;
import cz.sli0095.promod.entities.User;
import cz.sli0095.promod.entities.snapshots.SnapshotItem;
import cz.sli0095.promod.repositories.snapshots.SnapshotRoleRepository;
import cz.sli0095.promod.entities.snapshots.SnapshotRole;
import cz.sli0095.promod.repositories.RoleRepository;
import cz.sli0095.promod.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SnapshotRoleService {
    @Autowired
    SnapshotRoleRepository snapshotRoleRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    RoleService roleService;

    public SnapshotRole createSnapshotRole(Role original, SnapshotItem snapshotDetail, SnapshotsHelper helper){
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

        snapshot.setSnapshotName(snapshotDetail.getSnapshotName());
        snapshot.setSnapshotDescription(snapshotDetail.getSnapshotDescription());
        snapshot.setSnapshotDate(LocalDate.now());
        snapshot.setOriginalRole(original);
        snapshot.setOriginalId(original.getId());

        snapshot =  snapshotRoleRepository.save(snapshot);
        helper.addRole(original.getId(), snapshot);
        return snapshot;
    }

    public Role restoreRoleFromSnapshot(SnapshotRole snapshot, SnapshotsHelper helper, User user){
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

        role.setOwner(user);

        role = roleRepository.save(role);
        helper.addRole(snapshot.getId(), role);
        return role;
    }

    public SnapshotRole getSnapshotRoleById(long id){
        Optional<SnapshotRole> roleData = snapshotRoleRepository.findById(id);
        return roleData.orElse(null);
    }

    public Role revertRoleFromSnapshot(SnapshotRole snapshotRole, SnapshotsHelper helper){
        if(helper == null){
            helper = new SnapshotsHelper();
        }
        Role role = roleService.getRoleById(snapshotRole.getOriginalId());
        role.setName(snapshotRole.getName());
        role.setBriefDescription(snapshotRole.getBriefDescription());
        role.setMainDescription(snapshotRole.getMainDescription());
        role.setAssignmentApproaches(snapshotRole.getAssignmentApproaches());
        role.setSkills(snapshotRole.getSkills());
        role.setChangeDate(snapshotRole.getChangeDate());
        role.setChangeDescription(snapshotRole.getChangeDescription());
        role.setVersion(snapshotRole.getVersion());
        role = roleRepository.save(role);
        helper.addRole(snapshotRole.getId(), role);
        return role;
    }

    public boolean existRole(long id){
        return roleRepository.existsById(id);
    }

}
