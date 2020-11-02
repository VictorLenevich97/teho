package va.rit.teho.repository.labordistribution;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;

@Repository
public interface WorkhoursDistributionIntervalRepository extends CrudRepository<WorkhoursDistributionInterval, Long> {
}
