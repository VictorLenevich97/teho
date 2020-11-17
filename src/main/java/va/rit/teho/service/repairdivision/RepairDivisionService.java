package va.rit.teho.service.repairdivision;

import org.springframework.data.util.Pair;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.repairdivision.RepairDivisionUnit;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitEquipmentStaff;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RepairDivisionService {

    List<RepairDivisionUnit> listUnits(List<Long> filterIds);

    Pair<RepairDivisionUnit, List<RepairDivisionUnitEquipmentStaff>> getUnitWithStaff(Long repairDivisionUntId, UUID sessionId);

    Long addUnit(String name, Long typeId, Long stationTypeId, int amount);

    void updateUnit(Long id, String name, Long typeId, Long stationTypeId, int amount);

    void updateUnitEquipmentStaff(List<RepairDivisionUnitEquipmentStaff> repairDivisionUnitEquipmentStaffList);

    Map<RepairDivisionUnit, Map<EquipmentSubType, RepairDivisionUnitEquipmentStaff>> getRepairDivisionUnitEquipmentStaffGrouped(UUID sessionId,
                                                                                                                                List<Long> repairDivisionUnitIds,
                                                                                                                                List<Long> equipmentTypeIds,
                                                                                                                                List<Long> equipmentSubTypeIds);

    List<RepairDivisionUnitEquipmentStaff> listRepairDivisionUnitEquipmentStaff(Long repairDivisionUnitId, UUID sessionId);

    List<RepairDivisionUnitEquipmentStaff> listRepairDivisionUnitEquipmentStaff(UUID sessionId);

    void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId);

}
