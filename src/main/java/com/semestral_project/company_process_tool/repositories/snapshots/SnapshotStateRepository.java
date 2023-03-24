package com.semestral_project.company_process_tool.repositories.snapshots;

import com.semestral_project.company_process_tool.entities.snapshots.SnapshotState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapshotStateRepository extends CrudRepository<SnapshotState, Long> {
}
