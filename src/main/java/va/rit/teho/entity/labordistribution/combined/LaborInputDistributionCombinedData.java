package va.rit.teho.entity.labordistribution.combined;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;

import java.util.List;
import java.util.Map;

public class LaborInputDistributionCombinedData {

    private final List<EquipmentType> equipmentTypes;
    private final List<RepairType> repairTypes;
    private final List<WorkhoursDistributionInterval> workhoursDistributionIntervals;
    private final Map<EquipmentType, List<EquipmentLaborInputDistribution>> laborInputDistribution;

    public LaborInputDistributionCombinedData(List<EquipmentType> equipmentTypes,
                                              List<RepairType> repairTypes,
                                              Map<EquipmentType, List<EquipmentLaborInputDistribution>> laborInputDistribution,
                                              List<WorkhoursDistributionInterval> workhoursDistributionIntervals) {
        this.equipmentTypes = equipmentTypes;
        this.repairTypes = repairTypes;
        this.laborInputDistribution = laborInputDistribution;
        this.workhoursDistributionIntervals = workhoursDistributionIntervals;
    }

    public List<RepairType> getRepairTypes() {
        return repairTypes;
    }

    public List<EquipmentType> getEquipmentTypes() {
        return equipmentTypes;
    }

    public Map<EquipmentType, List<EquipmentLaborInputDistribution>> getLaborInputDistribution() {
        return laborInputDistribution;
    }

    public List<WorkhoursDistributionInterval> getWorkhoursDistributionIntervals() {
        return workhoursDistributionIntervals;
    }
}
