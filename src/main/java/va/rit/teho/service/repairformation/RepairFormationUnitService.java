package va.rit.teho.service.repairformation;

import org.springframework.data.util.Pair;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RepairFormationUnitService {

    List<RepairFormationUnit> list(List<Long> filterIds, Integer pageNum, Integer pageSize);

    List<RepairFormationUnit> list(Long repairFormationId, List<Long> filterIds, Integer pageNum, Integer pageSize);

    Pair<RepairFormationUnit, List<RepairFormationUnitEquipmentStaff>> getWithStaff(Long repairFormationUntId,
                                                                                    UUID sessionId);

    Long add(String name, Long repairFormationId, Long stationTypeId, int amount);

    void update(Long id, String name, Long repairFormationId, Long stationTypeId, int amount);

    void updateEquipmentStaff(List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList);

    Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> getWithEquipmentStaffGrouped(UUID sessionId,
                                                                                                                    List<Long> repairFormationUnitIds,
                                                                                                                    List<Long> equipmentTypeIds,
                                                                                                                    List<Long> equipmentSubTypeIds);

    List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(Long repairFormationUnitId, UUID sessionId);

    List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(UUID sessionId);

    void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId);

}
