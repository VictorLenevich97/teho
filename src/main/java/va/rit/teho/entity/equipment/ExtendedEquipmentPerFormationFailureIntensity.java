package va.rit.teho.entity.equipment;

public abstract class ExtendedEquipmentPerFormationFailureIntensity {

    private final Long formationId;
    private final Long equipmentId;
    private final Long stageId;
    private final Long repairTypeId;
    private final int failureIntensity;
    private final Double avgDailyFailure;

    public ExtendedEquipmentPerFormationFailureIntensity(Long formationId,
                                                         Long equipmentId,
                                                         Long stageId,
                                                         Long repairTypeId,
                                                         int failureIntensity,
                                                         Double avgDailyFailure) {
        this.formationId = formationId;
        this.equipmentId = equipmentId;
        this.stageId = stageId;
        this.repairTypeId = repairTypeId;
        this.failureIntensity = failureIntensity;
        this.avgDailyFailure = avgDailyFailure;
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

    public int getFailureIntensity() {
        return failureIntensity;
    }

    public Long getRepairTypeId() {
        return repairTypeId;
    }
}
