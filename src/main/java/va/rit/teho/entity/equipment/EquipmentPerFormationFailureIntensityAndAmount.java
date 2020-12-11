package va.rit.teho.entity.equipment;

public class EquipmentPerFormationFailureIntensityAndAmount extends ExtendedEquipmentPerFormationFailureIntensity {

    private final int equipmentAmount;

    public EquipmentPerFormationFailureIntensityAndAmount(Long formationId,
                                                          Long equipmentId,
                                                          Long stageId,
                                                          Long repairTypeId,
                                                          int failureIntensity,
                                                          int equipmentAmount) {
        super(formationId, equipmentId, stageId, repairTypeId, failureIntensity, null);
        this.equipmentAmount = equipmentAmount;
    }

    public int getEquipmentAmount() {
        return equipmentAmount;
    }

}
