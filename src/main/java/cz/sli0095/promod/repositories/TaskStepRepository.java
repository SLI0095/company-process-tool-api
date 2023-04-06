package cz.sli0095.promod.repositories;

import cz.sli0095.promod.entities.TaskStep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStepRepository extends CrudRepository<TaskStep, Long> {
}
