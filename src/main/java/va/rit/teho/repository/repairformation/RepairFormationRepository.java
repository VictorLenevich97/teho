package va.rit.teho.repository.repairformation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairformation.RepairFormation;

import java.util.List;

@Repository
public interface RepairFormationRepository extends CrudRepository<RepairFormation, Long> {

    List<RepairFormation> findAllByFormationId(Long formationId);

}
