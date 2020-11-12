package va.rit.teho.entity.equipment;

public class EquipmentPerBaseFailureIntensityAndLaborInput extends ExtendedEquipmentPerBaseFailureIntensity {

    private final int laborInput;

    public EquipmentPerBaseFailureIntensityAndLaborInput(Long baseId,
                                                         Long equipmentId,
                                                         Long stageId,
                                                         Long repairTypeId,
                                                         int failureIntensity,
                                                         Double avgDailyFailure,
                                                         int laborInput) {
        super(baseId, equipmentId, stageId, repairTypeId, failureIntensity, avgDailyFailure);
        this.laborInput = laborInput;
    }

    public int getLaborInput() {
        return laborInput;
    }

}
