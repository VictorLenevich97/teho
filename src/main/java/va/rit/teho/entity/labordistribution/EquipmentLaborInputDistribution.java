package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.common.RepairType;

import java.util.Map;
import java.util.Objects;

public class EquipmentLaborInputDistribution {

    private final String formationName;
    private final String equipmentName;
    private final int equipmentAmount;
    private final double avgDailyFailure;
    private final int standardLaborInput;
    private final Map<RepairType, CountAndLaborInputCombinedData> countAndLaborInputCombinedData;
    private final double totalRepairComplexity;

    public EquipmentLaborInputDistribution(
            String formationName,
            String equipmentName,
            int equipmentAmount,
            double avgDailyFailure,
            int standardLaborInput,
            Map<RepairType, CountAndLaborInputCombinedData> countAndLaborInputCombinedData,
            double totalRepairComplexity) {
        this.formationName = formationName;
        this.equipmentName = equipmentName;
        this.equipmentAmount = equipmentAmount;
        this.avgDailyFailure = avgDailyFailure;
        this.standardLaborInput = standardLaborInput;
        this.countAndLaborInputCombinedData = countAndLaborInputCombinedData;
        this.totalRepairComplexity = totalRepairComplexity;
    }

    public int getEquipmentAmount() {
        return equipmentAmount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double getTotalRepairComplexity() {
        return totalRepairComplexity;
    }

    public String getFormationName() {
        return formationName;
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

    public Map<RepairType, CountAndLaborInputCombinedData> getCountAndLaborInputCombinedData() {
        return countAndLaborInputCombinedData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentLaborInputDistribution that = (EquipmentLaborInputDistribution) o;
        return Double.compare(that.avgDailyFailure, avgDailyFailure) == 0 &&
                standardLaborInput == that.standardLaborInput &&
                Double.compare(that.totalRepairComplexity, totalRepairComplexity) == 0 &&
                Objects.equals(formationName, that.formationName) &&
                Objects.equals(equipmentName, that.equipmentName) &&
                Objects.equals(countAndLaborInputCombinedData, that.countAndLaborInputCombinedData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formationName,
                            equipmentName,
                            avgDailyFailure,
                            standardLaborInput,
                            countAndLaborInputCombinedData,
                            totalRepairComplexity);
    }

    public static class Builder {
        private String formationName;
        private String equipmentName;
        private int equipmentAmount;
        private double avgDailyFailure;
        private int standardLaborInput;
        private Map<RepairType, CountAndLaborInputCombinedData> countAndLaborInputCombinedData;
        private double totalRepairComplexity;

        public Builder formationName(String formationName) {
            this.formationName = formationName;
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

        public Builder totalRepairComplexity(double totalRepairComplexity) {
            this.totalRepairComplexity = totalRepairComplexity;
            return this;
        }

        public Builder countAndLaborInputCombinedData(Map<RepairType, CountAndLaborInputCombinedData> countAndLaborInputCombinedData) {
            this.countAndLaborInputCombinedData = countAndLaborInputCombinedData;
            return this;
        }

        public Builder equipmentAmount(int equipmentAmount) {
            this.equipmentAmount = equipmentAmount;
            return this;
        }

        public EquipmentLaborInputDistribution build() {
            return new EquipmentLaborInputDistribution(formationName,
                                                       equipmentName,
                                                       equipmentAmount,
                                                       avgDailyFailure,
                                                       standardLaborInput,
                                                       countAndLaborInputCombinedData,
                                                       totalRepairComplexity);
        }

    }
}
