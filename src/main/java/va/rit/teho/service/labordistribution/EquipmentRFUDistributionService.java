package va.rit.teho.service.labordistribution;

import va.rit.teho.entity.labordistribution.EquipmentPerFormationDistributionData;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistribution;
import va.rit.teho.entity.repairformation.RepairFormationUnit;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EquipmentRFUDistributionService {

    void distribute(UUID sessionId,
                    List<Long> equipmentIds,
                    List<Long> formationIds,
                    List<Long> repairFormationUnitIds);

    void copy(UUID oldSessionId, UUID newSessionId);

    Map<RepairFormationUnit, List<EquipmentRFUDistribution>> listDistributedEquipment(UUID sessionId);

    List<EquipmentRFUDistribution> listRFUDistributedEquipment(Long repairFormationUnitId, UUID sessionId);

    List<EquipmentPerFormationDistributionData> listDistributionDataForFormation(UUID sessionId, Long formationId);

}
