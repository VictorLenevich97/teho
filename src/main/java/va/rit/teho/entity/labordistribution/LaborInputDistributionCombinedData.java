package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Map;

public class LaborInputDistributionCombinedData {

    private final Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution;
    private final List<WorkhoursDistributionInterval> workhoursDistributionIntervals;

    public LaborInputDistributionCombinedData(Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution,
                                              List<WorkhoursDistributionInterval> workhoursDistributionIntervals) {
        this.laborInputDistribution = laborInputDistribution;
        this.workhoursDistributionIntervals = workhoursDistributionIntervals;
    }

    public Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> getLaborInputDistribution() {
        return laborInputDistribution;
    }

    public List<WorkhoursDistributionInterval> getWorkhoursDistributionIntervals() {
        return workhoursDistributionIntervals;
    }
}
