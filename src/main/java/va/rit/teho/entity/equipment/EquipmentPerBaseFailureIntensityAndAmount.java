package va.rit.teho.entity.equipment;

public class EquipmentPerBaseFailureIntensityAndAmount extends ExtendedEquipmentPerBaseFailureIntensity {

    private final int equipmentAmount;

    public EquipmentPerBaseFailureIntensityAndAmount(Long baseId,
                                                     Long equipmentId,
                                                     Long stageId,
                                                     Long repairTypeId,
                                                     int failureIntensity,
                                                     int equipmentAmount) {
        super(baseId, equipmentId, stageId, repairTypeId, failureIntensity, null);
        this.equipmentAmount = equipmentAmount;
    }

    public int getEquipmentAmount() {
        return equipmentAmount;
    }

}
