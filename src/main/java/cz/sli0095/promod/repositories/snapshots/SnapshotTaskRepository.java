package cz.sli0095.promod.repositories.snapshots;

import cz.sli0095.promod.entities.snapshots.SnapshotTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapshotTaskRepository extends CrudRepository<SnapshotTask, Long> {
}
