package cz.sli0095.promod.repositories;

import cz.sli0095.promod.entities.BPMNfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BPMNfileRepository extends CrudRepository<BPMNfile, Long> {
}