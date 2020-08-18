package va.rit.teho.repository;

import va.rit.teho.entity.WorkhoursDistributionInterval;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkhoursDistributionIntervalRepository extends CrudRepository<WorkhoursDistributionInterval, Long> {
}
