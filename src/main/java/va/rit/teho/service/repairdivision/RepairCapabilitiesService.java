package va.rit.teho.service.repairdivision;

import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.repairdivision.RepairDivisionUnit;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Сервис по расчету/получению производственных возможностей РВО по ремонту ВВСТ.
 */
public interface RepairCapabilitiesService {

    void copyRepairCapabilities(UUID originalSessionId, UUID newSessionId);

    void calculateAndUpdateRepairCapabilities(UUID sessionId, Long repairTypeId);

    void calculateAndUpdateRepairCapabilitiesPerStation(UUID sessionId, Long repairDivisionUnitId, Long repairTypeId);

    Map<RepairDivisionUnit, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            Long repairTypeId,
            List<Long> repairDivisionUnitIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds);

}
