package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Map;

public class LaborInputDistributionCombinedData {

    private final Map<EquipmentType, List<EquipmentLaborInputDistribution>> laborInputDistribution;
    private final List<WorkhoursDistributionInterval> workhoursDistributionIntervals;

    public LaborInputDistributionCombinedData(Map<EquipmentType, List<EquipmentLaborInputDistribution>> laborInputDistribution,
                                              List<WorkhoursDistributionInterval> workhoursDistributionIntervals) {
        this.laborInputDistribution = laborInputDistribution;
        this.workhoursDistributionIntervals = workhoursDistributionIntervals;
    }

    public Map<EquipmentType, List<EquipmentLaborInputDistribution>> getLaborInputDistribution() {
        return laborInputDistribution;
    }

    public List<WorkhoursDistributionInterval> getWorkhoursDistributionIntervals() {
        return workhoursDistributionIntervals;
    }
}
