package cz.sli0095.promod.repositories.snapshots;

import cz.sli0095.promod.entities.snapshots.SnapshotState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapshotStateRepository extends CrudRepository<SnapshotState, Long> {
}
