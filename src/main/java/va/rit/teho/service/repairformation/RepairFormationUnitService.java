package va.rit.teho.service.repairformation;

import org.springframework.data.util.Pair;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RepairFormationUnitService {

    Long count(List<Long> filterIds);

    List<RepairFormationUnit> list(List<Long> filterIds, Integer pageNum, Integer pageSize);

    List<RepairFormationUnit> list(Long repairFormationId, List<Long> filterIds, Integer pageNum, Integer pageSize);

    RepairFormationUnit get(Long id);

    Pair<RepairFormationUnit, List<RepairFormationUnitEquipmentStaff>> getWithStaff(Long repairFormationUntId,
                                                                                    UUID sessionId);

    RepairFormationUnit add(String name, Long repairFormationId, Long stationTypeId, int amount);

    RepairFormationUnit update(Long id, String name, Long repairFormationId, Long stationTypeId, int amount);

    List<RepairFormationUnitEquipmentStaff> updateEquipmentStaff(List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList);

    Map<EquipmentType, RepairFormationUnitEquipmentStaff> getEquipmentStaffPerType(UUID sessionId,
                                                                                   Long repairFormationUnitId,
                                                                                   List<Long> equipmentTypeIds);

    Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> listEquipmentStaffPerType(
            UUID sessionId,
            List<Long> repairFormationUnitIds,
            List<Long> equipmentTypeIds);

    List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(Long repairFormationUnitId, UUID sessionId);

    List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(UUID sessionId);

    void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId);

    void delete(Long id);

}
