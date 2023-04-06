package cz.sli0095.promod.repositories;

import cz.sli0095.promod.entities.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends CrudRepository<State, Long> {
}
