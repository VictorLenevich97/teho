package va.rit.teho.repository.labordistribution;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.labordistribution.RestorationType;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;

import java.util.Optional;

@Repository
public interface WorkhoursDistributionIntervalRepository extends CrudRepository<WorkhoursDistributionInterval, Long> {

    @Query("SELECT COALESCE(max(wdi.id), 0) FROM WorkhoursDistributionInterval wdi")
    Long getMaxId();

    Optional<WorkhoursDistributionInterval> findByLowerBoundAndUpperBoundAndRestorationType(Integer lowerBound,
                                                                                            Integer upperBound,
                                                                                            RestorationType restorationType);
}
