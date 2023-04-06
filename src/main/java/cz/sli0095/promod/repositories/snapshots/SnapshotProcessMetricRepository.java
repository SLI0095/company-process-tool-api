package cz.sli0095.promod.repositories.snapshots;

import cz.sli0095.promod.entities.snapshots.SnapshotProcessMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapshotProcessMetricRepository extends CrudRepository<SnapshotProcessMetric, Long>{
}
