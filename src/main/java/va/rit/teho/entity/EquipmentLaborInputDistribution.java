package va.rit.teho.entity;

import java.util.Map;

public class EquipmentLaborInputDistribution {

    private final Base base;
    private final EquipmentType equipmentType;
    private final Equipment equipment;
    private final double avgDailyFailure;
    private final int standardLaborInput;
    private final Map<WorkhoursDistributionInterval, CountAndLaborInput> intervalCountAndLaborInputMap;
    private final double totalRepairComplexity;

    public EquipmentLaborInputDistribution(
            Base base,
            EquipmentType equipmentType,
            Equipment equipment,
            double avgDailyFailure,
            int standardLaborInput,
            Map<WorkhoursDistributionInterval, CountAndLaborInput> intervalCountAndLaborInputMap,
            double totalRepairComplexity) {
        this.base = base;
        this.equipmentType = equipmentType;
        this.equipment = equipment;
        this.avgDailyFailure = avgDailyFailure;
        this.standardLaborInput = standardLaborInput;
        this.intervalCountAndLaborInputMap = intervalCountAndLaborInputMap;
        this.totalRepairComplexity = totalRepairComplexity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double getTotalRepairComplexity() {
        return totalRepairComplexity;
    }

    public Base getBase() {
        return base;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public int getStandardLaborInput() {
        return standardLaborInput;
    }

    public Map<WorkhoursDistributionInterval, CountAndLaborInput> getIntervalCountAndLaborInputMap() {
        return intervalCountAndLaborInputMap;
    }

    public static class Builder {
        private Base base;
        private EquipmentType equipmentType;
        private Equipment equipment;
        private double avgDailyFailure;
        private int standardLaborInput;
        private Map<WorkhoursDistributionInterval, CountAndLaborInput> intervalCountAndLaborInputMap;
        private double totalRepairComplexity;

        public Builder base(Base base) {
            this.base = base;
            return this;
        }

        public Builder equipmentType(EquipmentType equipmentType) {
            this.equipmentType = equipmentType;
            return this;
        }

        public Builder equipment(Equipment equipment) {
            this.equipment = equipment;
            return this;
        }

        public Builder avgDailyFailure(double avgDailyFailure) {
            this.avgDailyFailure = avgDailyFailure;
            return this;
        }

        public Builder standardLaborInput(int standardLaborInput) {
            this.standardLaborInput = standardLaborInput;
            return this;
        }

        public Builder intervalCountAndLaborInputMap(Map<WorkhoursDistributionInterval, CountAndLaborInput> intervalCountAndLaborInputMap) {
            this.intervalCountAndLaborInputMap = intervalCountAndLaborInputMap;
            return this;
        }

        public Builder totalRepairComplexity(double totalRepairComplexity) {
            this.totalRepairComplexity = totalRepairComplexity;
            return this;
        }

        public EquipmentLaborInputDistribution build() {
            return new EquipmentLaborInputDistribution(base,
                                                       equipmentType,
                                                       equipment,
                                                       avgDailyFailure,
                                                       standardLaborInput,
                                                       intervalCountAndLaborInputMap,
                                                       totalRepairComplexity);
        }

        public Map<WorkhoursDistributionInterval, CountAndLaborInput> getIntervalCountAndLaborInputMap() {
            return intervalCountAndLaborInputMap;
        }
    }
}
