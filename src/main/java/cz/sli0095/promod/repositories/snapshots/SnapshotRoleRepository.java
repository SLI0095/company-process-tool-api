package cz.sli0095.promod.repositories.snapshots;

import cz.sli0095.promod.entities.snapshots.SnapshotRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapshotRoleRepository extends CrudRepository<SnapshotRole, Long> {
}
