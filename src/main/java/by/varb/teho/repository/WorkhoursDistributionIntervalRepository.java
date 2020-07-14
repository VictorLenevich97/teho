package by.varb.teho.repository;

import by.varb.teho.entity.WorkhoursDistributionInterval;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkhoursDistributionIntervalRepository extends CrudRepository<WorkhoursDistributionInterval, Long> {
}
