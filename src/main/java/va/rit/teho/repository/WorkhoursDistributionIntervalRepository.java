package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.WorkhoursDistributionInterval;

@Repository
public interface WorkhoursDistributionIntervalRepository extends CrudRepository<WorkhoursDistributionInterval, Long> {
}
