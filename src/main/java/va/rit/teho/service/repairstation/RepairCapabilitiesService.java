package va.rit.teho.service.repairstation;

import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.repairstation.RepairStation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Сервис по расчету/получению производственных возможностей РВО по ремонту ВВСТ.
 */
public interface RepairCapabilitiesService {

    void copyRepairCapabilities(UUID originalSessionId, UUID newSessionId);

    void calculateAndUpdateRepairCapabilities(UUID sessionId, Long repairTypeId);

    void calculateAndUpdateRepairCapabilitiesPerStation(UUID sessionId, Long repairStationId, Long repairTypeId);

    Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            Long repairTypeId,
            List<Long> repairStationIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds);

}
