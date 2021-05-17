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

    void calculateAndUpdateRepairCapabilities(UUID sessionId);

    /**
     * Произвести расчет произв. возможностей для конкретного РВО
     * @param sessionId - ID сессии
     * @param repairFormationUnitId - ID РВО
     */
    void calculateAndUpdateRepairCapabilitiesPerRFU(UUID sessionId, Long repairFormationUnitId);

    /**
     * Ручное обновление произв. возможностей конкретного РВО (для многих ВВСТ)
     * @param sessionId - ID сессии
     * @param repairFormationUnitId - ID РВО
     * @param capabilitiesMap - Map, где ключи это ID ВВСТ, а значения - произв. возможности по ремонту (ед./сут.), заданные вручную
     */
    void updateRepairCapabilities(UUID sessionId,
                                  Long repairFormationUnitId,
                                  Map<Long, Double> capabilitiesMap);
    /**
     * Ручное обновление произв. возможностей конкретного РВО (для одного ВВСТ)
     * @param sessionId - ID сессии
     * @param repairFormationUnitId - ID РВО
     * @param equipmentId - ID ВВСТ
     * @param capability - произв. возможности по ремонту (ед./сут.)
     */
    RepairFormationUnitRepairCapability updateRepairCapabilities(UUID sessionId,
                                                                 Long repairFormationUnitId,
                                                                 Long equipmentId,
                                                                 Double capability);

    Map<Equipment, Double> getCalculatedRepairCapabilities(
            UUID sessionId,
            Long repairFormationUnitId,
            List<Long> equipmentIds,
            List<Long> equipmentTypeIds);

    Map<RepairFormationUnit, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            List<Long> repairFormationUnitIds,
            List<Long> equipmentIds,
            List<Long> equipmentTypeIds);

}
