package va.rit.teho.repository.repairdivision;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitType;

import java.util.Optional;

@Repository
public interface RepairDivisionUnitTypeRepository extends CrudRepository<RepairDivisionUnitType, Long> {
    Optional<RepairDivisionUnitType> findByName(String name);
}
