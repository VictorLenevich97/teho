package va.rit.teho.repository.repairformation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairformation.RepairFormation;

@Repository
public interface RepairFormationRepository extends CrudRepository<RepairFormation, Long> {

}
