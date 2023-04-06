package cz.sli0095.promod.repositories;

import cz.sli0095.promod.entities.ProcessMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessMetricRepository extends CrudRepository<ProcessMetric, Long> {
}
