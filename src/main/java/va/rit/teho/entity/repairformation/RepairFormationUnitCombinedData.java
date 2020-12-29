package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Map;

public class RepairFormationUnitCombinedData {
    private final List<RepairFormationUnit> repairFormationUnitList;
    private final Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes;
    private final Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> repairFormationUnitEquipmentStaff;

    public RepairFormationUnitCombinedData(List<RepairFormationUnit> repairFormationUnitList,
                                           Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes,
                                           Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> repairFormationUnitEquipmentStaff) {
        this.repairFormationUnitList = repairFormationUnitList;
        this.typesWithSubTypes = typesWithSubTypes;
        this.repairFormationUnitEquipmentStaff = repairFormationUnitEquipmentStaff;
    }

    public List<RepairFormationUnit> getRepairFormationUnitList() {
        return repairFormationUnitList;
    }

    public Map<EquipmentType, List<EquipmentSubType>> getTypesWithSubTypes() {
        return typesWithSubTypes;
    }

    public Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> getRepairFormationUnitEquipmentStaff() {
        return repairFormationUnitEquipmentStaff;
    }
}
