package va.rit.teho.entity;

import java.util.Map;

public class EquipmentLaborInputDistribution {

    private final String baseName;
    private final EquipmentType equipmentType;

    public EquipmentSubType getEquipmentSubType() {
        return equipmentSubType;
    }

    private final EquipmentSubType equipmentSubType;
    private final String equipmentName;
    private final double avgDailyFailure;
    private final int standardLaborInput;
    private final Map<Long, CountAndLaborInput> intervalCountAndLaborInputMap;
    private final double totalRepairComplexity;

    public EquipmentLaborInputDistribution(
            String baseName,
            EquipmentType equipmentType,
            EquipmentSubType equipmentSubType,
            String equipmentName,
            double avgDailyFailure,
            int standardLaborInput,
            Map<Long, CountAndLaborInput> intervalCountAndLaborInputMap,
            double totalRepairComplexity) {
        this.baseName = baseName;
        this.equipmentType = equipmentType;
        this.equipmentSubType = equipmentSubType;
        this.equipmentName = equipmentName;
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

    public String getBaseName() {
        return baseName;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public int getStandardLaborInput() {
        return standardLaborInput;
    }

    public Map<Long, CountAndLaborInput> getIntervalCountAndLaborInputMap() {
        return intervalCountAndLaborInputMap;
    }

    public static class Builder {
        private String baseName;
        private EquipmentType equipmentType;
        private EquipmentSubType equipmentSubType;
        private String equipmentName;
        private double avgDailyFailure;
        private int standardLaborInput;
        private Map<Long, CountAndLaborInput> intervalCountAndLaborInputMap;
        private double totalRepairComplexity;

        public Builder baseName(String baseName) {
            this.baseName = baseName;
            return this;
        }

        public Builder equipmentType(EquipmentType equipmentType) {
            this.equipmentType = equipmentType;
            return this;
        }

        public Builder equipmentSubType(EquipmentSubType equipmentSubType) {
            this.equipmentSubType = equipmentSubType;
            return this;
        }

        public Builder equipmentName(String equipmentName) {
            this.equipmentName = equipmentName;
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

        public Builder intervalCountAndLaborInputMap(Map<Long, CountAndLaborInput> intervalCountAndLaborInputMap) {
            this.intervalCountAndLaborInputMap = intervalCountAndLaborInputMap;
            return this;
        }

        public Builder totalRepairComplexity(double totalRepairComplexity) {
            this.totalRepairComplexity = totalRepairComplexity;
            return this;
        }

        public EquipmentLaborInputDistribution build() {
            return new EquipmentLaborInputDistribution(baseName,
                                                       equipmentType,
                                                       equipmentSubType,
                                                       equipmentName,
                                                       avgDailyFailure,
                                                       standardLaborInput,
                                                       intervalCountAndLaborInputMap,
                                                       totalRepairComplexity);
        }

        public Map<Long, CountAndLaborInput> getIntervalCountAndLaborInputMap() {
            return intervalCountAndLaborInputMap;
        }
    }
}
