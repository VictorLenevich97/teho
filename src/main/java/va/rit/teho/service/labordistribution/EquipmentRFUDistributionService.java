package va.rit.teho.service.labordistribution;

import va.rit.teho.entity.labordistribution.EquipmentPerFormationDistributionData;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistribution;

import java.util.List;
import java.util.UUID;

public interface EquipmentRFUDistributionService {

    void distribute(UUID sessionId);

    void copy(UUID oldSessionId, UUID newSessionId);

    List<EquipmentRFUDistribution> listRFUDistributedEquipment(Long repairFormationUnitId, UUID sessionId);

    List<EquipmentPerFormationDistributionData> listDistributionDataForFormation(UUID sessionId, Long formationId);

}
