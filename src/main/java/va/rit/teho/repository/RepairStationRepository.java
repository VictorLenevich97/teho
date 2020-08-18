package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.RepairStation;

@Repository
public interface RepairStationRepository extends CrudRepository<RepairStation, Long> {
}
