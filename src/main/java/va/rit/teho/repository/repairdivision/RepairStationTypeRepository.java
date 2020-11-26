package va.rit.teho.repository.repairdivision;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairdivision.RepairStationType;

import java.util.Optional;

@Repository
public interface RepairStationTypeRepository extends CrudRepository<RepairStationType, Long> {

    Optional<RepairStationType> findByName(String name);
}
