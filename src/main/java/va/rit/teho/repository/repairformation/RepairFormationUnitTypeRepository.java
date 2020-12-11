package va.rit.teho.repository.repairformation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairformation.RepairFormationType;

import java.util.Optional;

@Repository
public interface RepairFormationUnitTypeRepository extends CrudRepository<RepairFormationType, Long> {
    Optional<RepairFormationType> findByName(String name);
}
