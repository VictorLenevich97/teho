package va.rit.teho.service.repairformation;

import org.springframework.data.domain.PageRequest;
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

public interface RepairFormationUnitService {

    Long count(List<Long> filterIds);

    List<RepairFormationUnit> list(UUID sessionId, List<Long> filterIds, PageRequest pagination);

    List<RepairFormationUnit> list(UUID sessionId, RepairFormation repairFormation, List<Long> filterIds, PageRequest pagination);

    RepairFormationUnit get(Long id);

    RepairFormationUnit add(String name,
                            RepairFormation repairFormation,
                            WorkhoursDistributionInterval interval,
                            RepairType repairType,
                            RepairStationType repairStationType,
                            int stationAmount);

    RepairFormationUnit update(Long id,
                               String name,
                               RepairFormation repairFormation,
                               WorkhoursDistributionInterval interval,
                               RepairType repairType,
                               RepairStationType repairStationType,
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
