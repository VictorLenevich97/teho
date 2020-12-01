package va.rit.teho.service.repairformation;

import org.springframework.data.util.Pair;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RepairFormationService {

    List<RepairFormationUnit> listUnits(List<Long> filterIds, Integer pageNum, Integer pageSize);

    Pair<RepairFormationUnit, List<RepairFormationUnitEquipmentStaff>> getUnitWithStaff(Long repairFormationUntId, UUID sessionId);

    Long addUnit(String name, Long repairFormationId, Long stationTypeId, int amount);

    void updateUnit(Long id, String name,Long repairFormationId, Long stationTypeId, int amount);

    void updateUnitEquipmentStaff(List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList);

    Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> getRepairFormationUnitEquipmentStaffGrouped(UUID sessionId,
                                                                                                                                   List<Long> repairFormationUnitIds,
                                                                                                                                   List<Long> equipmentTypeIds,
                                                                                                                                   List<Long> equipmentSubTypeIds);

    List<RepairFormationUnitEquipmentStaff> listRepairFormationUnitEquipmentStaff(Long repairFormationUnitId, UUID sessionId);

    List<RepairFormationUnitEquipmentStaff> listRepairFormationUnitEquipmentStaff(UUID sessionId);

    void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId);

}
