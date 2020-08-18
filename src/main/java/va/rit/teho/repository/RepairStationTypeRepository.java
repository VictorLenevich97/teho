package va.rit.teho.repository;

import va.rit.teho.entity.RepairStationType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairStationTypeRepository extends CrudRepository<RepairStationType, Long> {
}
