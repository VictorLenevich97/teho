package va.rit.teho.service.repairstation;

import org.springframework.data.util.Pair;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.repairstation.RepairStation;
import va.rit.teho.entity.repairstation.RepairStationEquipmentStaff;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RepairStationService {

    List<RepairStation> list(List<Long> filterIds);

    Pair<RepairStation, List<RepairStationEquipmentStaff>> get(Long repairStationId, UUID sessionId);

    Long add(String name, Long baseId, Long typeId, int amount);

    void update(Long id, String name, Long baseId, Long typeId, int amount);

    void updateEquipmentStaff(List<RepairStationEquipmentStaff> repairStationEquipmentStaffList);

    Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> getRepairStationEquipmentStaffGrouped(UUID sessionId,
                                                                                                                 List<Long> repairStationIds,
                                                                                                                 List<Long> equipmentTypeIds,
                                                                                                                 List<Long> equipmentSubTypeIds);

    List<RepairStationEquipmentStaff> listRepairStationEquipmentStaff(Long repairStationId, UUID sessionId);

    List<RepairStationEquipmentStaff> listRepairStationEquipmentStaff(UUID sessionId);

    void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId);

}
