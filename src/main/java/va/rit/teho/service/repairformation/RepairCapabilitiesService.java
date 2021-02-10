package va.rit.teho.service.repairformation;

import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Сервис по расчету/получению производственных возможностей РВО по ремонту ВВСТ.
 */
public interface RepairCapabilitiesService {

    void copyRepairCapabilities(UUID originalSessionId, UUID newSessionId);

    void calculateAndUpdateRepairCapabilities(UUID sessionId, Long repairTypeId);

    void calculateAndUpdateRepairCapabilitiesPerStation(UUID sessionId, Long repairFormationUnitId, Long repairTypeId);

    void updateRepairCapabilities(UUID sessionId,
                                  Long repairFormationUnitId,
                                  Long repairTypeId,
                                  Map<Long, Double> capabilitiesMap);

    RepairFormationUnitRepairCapability updateRepairCapabilities(UUID sessionId,
                                                                 Long repairFormationUnitId,
                                                                 Long repairTypeId,
                                                                 Long equipmentId,
                                                                 Double capability);

    Map<Equipment, Double> getCalculatedRepairCapabilities(
            UUID sessionId,
            Long repairFormationUnitId,
            Long repairTypeId,
            List<Long> equipmentIds,
            List<Long> equipmentTypeIds);

    Map<RepairFormationUnit, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            Long repairTypeId,
            List<Long> repairFormationUnitIds,
            List<Long> equipmentIds,
            List<Long> equipmentTypeIds);

}
