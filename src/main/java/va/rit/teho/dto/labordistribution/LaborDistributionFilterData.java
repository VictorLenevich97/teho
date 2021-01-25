package va.rit.teho.dto.labordistribution;

import java.util.List;

public class LaborDistributionFilterData {
    private final List<Long> equipmentIds;
    private final List<Long> formationIds;
    private final List<Long> repairFormationUnitIds;

    public LaborDistributionFilterData() {
        this.equipmentIds = null;
        this.formationIds = null;
        this.repairFormationUnitIds = null;
    }

    public LaborDistributionFilterData(List<Long> equipmentIds,
                                       List<Long> formationIds,
                                       List<Long> repairFormationUnitIds) {
        this.equipmentIds = equipmentIds;
        this.formationIds = formationIds;
        this.repairFormationUnitIds = repairFormationUnitIds;
    }

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public List<Long> getFormationIds() {
        return formationIds;
    }

    public List<Long> getRepairFormationUnitIds() {
        return repairFormationUnitIds;
    }
}
