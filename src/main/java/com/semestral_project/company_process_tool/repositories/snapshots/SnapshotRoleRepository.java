package com.semestral_project.company_process_tool.repositories.snapshots;

import com.semestral_project.company_process_tool.entities.snapshots.SnapshotRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapshotRoleRepository extends CrudRepository<SnapshotRole, Long> {
}
