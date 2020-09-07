package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.RepairStationType;

import java.util.Optional;

@Repository
public interface RepairStationTypeRepository extends CrudRepository<RepairStationType, Long> {

    Optional<RepairStationType> findByName(String name);
}
