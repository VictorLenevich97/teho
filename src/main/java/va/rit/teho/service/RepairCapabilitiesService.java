package va.rit.teho.service;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Сервис по расчету/получению производственных возможностей РВО по ремонту ВВСТ.
 */
public interface RepairCapabilitiesService {

    void calculateAndUpdateRepairCapabilities(UUID sessionId, Long repairTypeId);

    void calculateAndUpdateRepairCapabilitiesPerStation(UUID sessionId, Long repairStationId, Long repairTypeId);

    Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            List<Long> repairStationIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds);

    Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            UUID sessionId,
            Long repairTypeId,
            List<Long> repairStationIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds);

    Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> getRepairStationEquipmentStaff(UUID sessionId,
                                                                                                          List<Long> repairStationIds,
                                                                                                          List<Long> equipmentTypeIds,
                                                                                                          List<Long> equipmentSubTypeIds);
}
