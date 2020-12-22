package va.rit.teho.repository.labordistribution;

import org.springframework.data.repository.CrudRepository;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistribution;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistributionPK;

import java.util.List;
import java.util.UUID;

public interface EquipmentRFUDistributionRepository
        extends CrudRepository<EquipmentRFUDistribution, EquipmentRFUDistributionPK> {

    List<EquipmentRFUDistribution> findByTehoSessionId(UUID sessionId);

    List<EquipmentRFUDistribution> findByRepairFormationUnitIdAndTehoSessionId(Long repairFormationUnitId, UUID sessionId);
}
