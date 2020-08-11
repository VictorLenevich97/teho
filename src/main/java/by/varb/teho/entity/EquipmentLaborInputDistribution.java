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
    private final int totalRepairComplexity;

    public EquipmentLaborInputDistribution(
            String baseName,
            EquipmentType equipmentType,
            Equipment equipment,
            double avgDailyFailure,
            int standardLaborInput,
            Map<WorkhoursDistributionInterval, CountAndLaborInput> intervalCountAndLaborInputMap,
            int totalRepairComplexity) {
        this.baseName = baseName;
        this.equipmentType = equipmentType;
        this.equipment = equipment;
        this.avgDailyFailure = avgDailyFailure;
        this.standardLaborInput = standardLaborInput;
        this.intervalCountAndLaborInputMap = intervalCountAndLaborInputMap;
        this.totalRepairComplexity = totalRepairComplexity;
    }

    public int getTotalRepairComplexity() {
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
        private final int count;
        private final int laborInput;

        public CountAndLaborInput(int count, int laborInput) {
            this.count = count;
            this.laborInput = laborInput;
        }

        public int getCount() {
            return count;
        }

        public int getLaborInput() {
            return laborInput;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CountAndLaborInput that = (CountAndLaborInput) o;
            return count == that.count &&
                    laborInput == that.laborInput;
        }

        @Override
        public int hashCode() {
            return Objects.hash(count, laborInput);
        }
    }
}
