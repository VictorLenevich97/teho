package va.rit.teho.entity.equipment;

public abstract class ExtendedEquipmentPerBaseFailureIntensity {

    private final Long baseId;
    private final Long equipmentId;
    private final Long stageId;
    private final Long repairTypeId;
    private final int failureIntensity;
    private final Double avgDailyFailure;

    public ExtendedEquipmentPerBaseFailureIntensity(Long baseId,
                                                    Long equipmentId,
                                                    Long stageId,
                                                    Long repairTypeId,
                                                    int failureIntensity,
                                                    Double avgDailyFailure) {
        this.baseId = baseId;
        this.equipmentId = equipmentId;
        this.stageId = stageId;
        this.repairTypeId = repairTypeId;
        this.failureIntensity = failureIntensity;
        this.avgDailyFailure = avgDailyFailure;
    }

    public Double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public Long getBaseId() {
        return baseId;
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
