package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Map;

public class RepairFormationUnitRepairCapabilityCombinedData {

    private final List<RepairFormationUnit> repairFormationUnitList;
    private final List<EquipmentType> equipmentTypes;
    private final Map<RepairFormationUnit, Map<Equipment, Double>> calculatedRepairCapabilities;

    public RepairFormationUnitRepairCapabilityCombinedData(List<RepairFormationUnit> repairFormationUnitList,
                                                           List<EquipmentType> equipmentTypes,
                                                           Map<RepairFormationUnit, Map<Equipment, Double>> calculatedRepairCapabilities) {
        this.repairFormationUnitList = repairFormationUnitList;
        this.equipmentTypes = equipmentTypes;
        this.calculatedRepairCapabilities = calculatedRepairCapabilities;
    }

    public List<RepairFormationUnit> getRepairFormationUnitList() {
        return repairFormationUnitList;
    }

    public List<EquipmentType> getEquipmentTypes() {
        return equipmentTypes;
    }

    public Map<RepairFormationUnit, Map<Equipment, Double>> getCalculatedRepairCapabilities() {
        return calculatedRepairCapabilities;
    }
}
