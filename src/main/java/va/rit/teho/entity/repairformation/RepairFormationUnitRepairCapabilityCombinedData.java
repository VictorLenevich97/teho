package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Map;

public class RepairFormationUnitRepairCapabilityCombinedData {

    private final List<RepairFormationUnit> repairFormationUnitList;
    private final Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> groupedEquipmentData;
    private final Map<RepairFormationUnit, Map<Equipment, Double>> calculatedRepairCapabilities;

    public RepairFormationUnitRepairCapabilityCombinedData(List<RepairFormationUnit> repairFormationUnitList,
                                                           Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped,
                                                           Map<RepairFormationUnit, Map<Equipment, Double>> calculatedRepairCapabilities) {
        this.repairFormationUnitList = repairFormationUnitList;
        this.groupedEquipmentData = grouped;
        this.calculatedRepairCapabilities = calculatedRepairCapabilities;
    }

    public List<RepairFormationUnit> getRepairFormationUnitList() {
        return repairFormationUnitList;
    }

    public Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> getGroupedEquipmentData() {
        return groupedEquipmentData;
    }

    public Map<RepairFormationUnit, Map<Equipment, Double>> getCalculatedRepairCapabilities() {
        return calculatedRepairCapabilities;
    }
}
