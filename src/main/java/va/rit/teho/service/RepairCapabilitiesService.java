package va.rit.teho.service;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;

import java.util.List;
import java.util.Map;

/**
 * Сервис по расчету/получению производственных возможностей РВО по ремонту ВВСТ.
 */
public interface RepairCapabilitiesService {

    void calculateAndUpdateRepairCapabilities(Long repairTypeId);

    void calculateAndUpdateRepairCapabilitiesPerStation(Long repairStationId, Long repairTypeId);

    Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            List<Long> repairStationIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds);

    Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(
            Long repairTypeId,
            List<Long> repairStationIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds);

    Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> getRepairStationEquipmentStaff(List<Long> repairStationIds, List<Long> equipmentTypeIds, List<Long> equipmentSubTypeIds);
}
