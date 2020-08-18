package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.RepairStationType;

@Repository
public interface RepairStationTypeRepository extends CrudRepository<RepairStationType, Long> {
}
