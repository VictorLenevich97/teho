package va.rit.teho.service.repairformation;

import org.springframework.data.util.Pair;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairStationType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RepairFormationUnitServiceFacade {

    Long count(List<Long> filterIds);

    List<RepairFormationUnit> list(UUID sessionId, List<Long> filterIds, Integer pageNum, Integer pageSize);

    List<RepairFormationUnit> list(UUID sessionId, Long repairFormationId, List<Long> filterIds, Integer pageNum, Integer pageSize);

    RepairFormationUnit get(Long id);

    Pair<RepairFormationUnit, List<RepairFormationUnitEquipmentStaff>> getWithStaff(UUID sessionId, Long repairFormationUntId);

    RepairFormationUnit add(String name,
                            Long repairFormationId,
                            Long intervalId,
                            Long repairTypeId,
                            Long repairStationTypeId,
                            int stationAmount);

    RepairFormationUnit update(Long id,
                               String name,
                               Long repairFormationId,
                               Long intervalId,
                               Long repairTypeId,
                               Long repairStationTypeId,
                               int amount);

    List<RepairFormationUnitEquipmentStaff> updateEquipmentStaff(List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList);

    Map<EquipmentType, RepairFormationUnitEquipmentStaff> getEquipmentStaffPerType(UUID sessionId,
                                                                                   Long repairFormationUnitId,
                                                                                   List<Long> equipmentTypeIds);

    Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> listEquipmentStaffPerType(
            UUID sessionId,
            List<Long> repairFormationUnitIds,
            List<Long> equipmentTypeIds);

    List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(UUID sessionId, Long repairFormationUnitId);

    List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(UUID sessionId);

    void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId);

    void delete(Long id);
}
