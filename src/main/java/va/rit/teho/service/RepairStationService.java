package va.rit.teho.service;

import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;
import va.rit.teho.model.Pair;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RepairStationService {

    List<RepairStation> list(List<Long> filterIds);

    Pair<RepairStation, List<RepairStationEquipmentStaff>> get(Long repairStationId);

    Long add(String name, Long baseId, Long typeId, int amount);

    void update(Long id, String name, Long baseId, Long typeId, int amount);

    void setEquipmentStaff(List<RepairStationEquipmentStaff> repairStationEquipmentStaffList);

    void updateEquipmentStaff(List<RepairStationEquipmentStaff> repairStationEquipmentStaffList);

    Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> getRepairStationEquipmentStaff(UUID sessionId,
                                                                                                          List<Long> repairStationIds,
                                                                                                          List<Long> equipmentTypeIds,
                                                                                                          List<Long> equipmentSubTypeIds);

}
