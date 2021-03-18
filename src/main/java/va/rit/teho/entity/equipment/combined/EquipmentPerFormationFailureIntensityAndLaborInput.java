package va.rit.teho.entity.equipment.combined;

public class EquipmentPerFormationFailureIntensityAndLaborInput {
    private final Long formationId;
    private final Long equipmentId;
    private final Long stageId;
    private final Long repairTypeId;
    private final Double avgDailyFailure;
    private final int laborInput;

    public EquipmentPerFormationFailureIntensityAndLaborInput(Long formationId,
                                                              Long equipmentId,
                                                              Long stageId,
                                                              Long repairTypeId,
                                                              Double avgDailyFailure,
                                                              int laborInput) {
        this.formationId = formationId;
        this.equipmentId = equipmentId;
        this.stageId = stageId;
        this.repairTypeId = repairTypeId;
        this.avgDailyFailure = avgDailyFailure;
        this.laborInput = laborInput;
    }

    public Double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public Long getFormationId() {
        return formationId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public Long getStageId() {
        return stageId;
    }

    public Long getRepairTypeId() {
        return repairTypeId;
    }

    public int getLaborInput() {
        return laborInput;
    }

}
