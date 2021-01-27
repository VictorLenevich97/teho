package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Map;

public class RepairFormationUnitCombinedData {
    private final List<RepairFormationUnit> repairFormationUnitList;
    private final List<EquipmentType> equipmentTypes;
    private final Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> repairFormationUnitEquipmentStaff;

    public RepairFormationUnitCombinedData(List<RepairFormationUnit> repairFormationUnitList,
                                           List<EquipmentType> equipmentTypes,
                                           Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> repairFormationUnitEquipmentStaff) {
        this.repairFormationUnitList = repairFormationUnitList;
        this.equipmentTypes = equipmentTypes;
        this.repairFormationUnitEquipmentStaff = repairFormationUnitEquipmentStaff;
    }

    public List<RepairFormationUnit> getRepairFormationUnitList() {
        return repairFormationUnitList;
    }

    public List<EquipmentType> getEquipmentTypes() {
        return equipmentTypes;
    }

    public Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> getRepairFormationUnitEquipmentStaff() {
        return repairFormationUnitEquipmentStaff;
    }
}
