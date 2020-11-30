package va.rit.teho.entity.equipment;

public class EquipmentPerFormationFailureIntensityAndLaborInput extends ExtendedEquipmentPerFormationFailureIntensity {

    private final int laborInput;

    public EquipmentPerFormationFailureIntensityAndLaborInput(Long formationId,
                                                              Long equipmentId,
                                                              Long stageId,
                                                              Long repairTypeId,
                                                              int failureIntensity,
                                                              Double avgDailyFailure,
                                                              int laborInput) {
        super(formationId, equipmentId, stageId, repairTypeId, failureIntensity, avgDailyFailure);
        this.laborInput = laborInput;
    }

    public int getLaborInput() {
        return laborInput;
    }

}
