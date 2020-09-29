package va.rit.teho.service;

import va.rit.teho.entity.RepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;
import va.rit.teho.model.Pair;

import java.util.List;
import java.util.UUID;

public interface RepairStationService {

    List<RepairStation> list(List<Long> filterIds);

    Pair<RepairStation, List<RepairStationEquipmentStaff>> get(Long repairStationId);

    Long add(String name, Long baseId, Long typeId, int amount);

    void update(Long id, String name, Long baseId, Long typeId, int amount);

    void setEquipmentStaff(UUID sessionId, Long repairStationId, Long equipmentId, int availableStaff, int totalStaff);

    void updateEquipmentStaff(UUID sessionId,
                              Long repairStationId,
                              Long equipmentId,
                              int availableStaff,
                              int totalStaff);

    List<RepairStationEquipmentStaff> listEquipmentStaff(UUID sessionId,
                                                         List<Long> repairStationIds,
                                                         List<Long> equipmentTypeIds,
                                                         List<Long> equipmentSubTypeIds);

}
