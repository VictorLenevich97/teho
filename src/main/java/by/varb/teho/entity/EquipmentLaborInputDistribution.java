package by.varb.teho.entity;

import java.util.Map;
import java.util.Objects;

public class EquipmentLaborInputDistribution {

    private final String baseName;
    private final EquipmentType equipmentType;
    private final Equipment equipment;
    private final double avgDailyFailure;
    private final int standardLaborInput;
    private final Map<WorkhoursDistributionInterval, CountAndLaborInput> intervalCountAndLaborInputMap;
    private final double totalRepairComplexity;

    public EquipmentLaborInputDistribution(
            String baseName,
            EquipmentType equipmentType,
            Equipment equipment,
            double avgDailyFailure,
            int standardLaborInput,
            Map<WorkhoursDistributionInterval, CountAndLaborInput> intervalCountAndLaborInputMap,
            double totalRepairComplexity) {
        this.baseName = baseName;
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

    public String getBaseName() {
        return baseName;
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

    public static class CountAndLaborInput {
        private final double count;
        private final double laborInput;

        public CountAndLaborInput(double count, double laborInput) {
            this.count = count;
            this.laborInput = laborInput;
        }

        public double getCount() {
            return count;
        }

        public double getLaborInput() {
            return laborInput;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CountAndLaborInput that = (CountAndLaborInput) o;
            return count == that.count &&
                    laborInput == that.laborInput;
        }

        @Override
        public int hashCode() {
            return Objects.hash(count, laborInput);
        }
    }

    public static class Builder {
        private String baseName;
        private EquipmentType equipmentType;
        private Equipment equipment;
        private double avgDailyFailure;
        private int standardLaborInput;
        private Map<WorkhoursDistributionInterval, CountAndLaborInput> intervalCountAndLaborInputMap;
        private double totalRepairComplexity;

        public Builder baseName(String baseName) {
            this.baseName = baseName;
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
            return new EquipmentLaborInputDistribution(baseName,
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
