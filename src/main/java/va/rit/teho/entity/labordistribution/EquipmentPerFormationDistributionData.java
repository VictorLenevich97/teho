package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;

import java.util.Map;

public class EquipmentPerFormationDistributionData {
    private final Equipment equipment;
    private final Integer amount;
    private final Double avgDailyFailure;
    private final Map<RepairType, Double> amountPerRepairType;
    private final Map<RestorationType, Double> amountPerRestorationType;

    public EquipmentPerFormationDistributionData(Equipment equipment,
                                                 Integer amount,
                                                 Double avgDailyFailure,
                                                 Map<RepairType, Double> amountPerRepairType,
                                                 Map<RestorationType, Double> amountPerRestorationType) {
        this.equipment = equipment;
        this.amount = amount;
        this.avgDailyFailure = avgDailyFailure;
        this.amountPerRepairType = amountPerRepairType;
        this.amountPerRestorationType = amountPerRestorationType;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Integer getAmount() {
        return amount;
    }

    public Double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public Map<RepairType, Double> getAmountPerRepairType() {
        return amountPerRepairType;
    }

    public Map<RestorationType, Double> getAmountPerRestorationType() {
        return amountPerRestorationType;
    }
}
