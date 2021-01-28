package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Map;

public class LaborInputDistributionCombinedData {

    private final List<EquipmentType> equipmentTypes;
    private final List<WorkhoursDistributionInterval> workhoursDistributionIntervals;
    private final Map<EquipmentType, List<EquipmentLaborInputDistribution>> laborInputDistribution;

    public LaborInputDistributionCombinedData(List<EquipmentType> equipmentTypes,
                                              Map<EquipmentType, List<EquipmentLaborInputDistribution>> laborInputDistribution,
                                              List<WorkhoursDistributionInterval> workhoursDistributionIntervals) {
        this.equipmentTypes = equipmentTypes;
        this.laborInputDistribution = laborInputDistribution;
        this.workhoursDistributionIntervals = workhoursDistributionIntervals;
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
