package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.EquipmentSubType;

import java.util.Objects;

public class LaborDistributionData {

    private CompositeKey compositeKey;
    private String formationName;
    private String equipmentName;
    private Integer laborInput;
    private Long intervalId;
    private Double count;
    private Double avgLaborInput;
    private Double avgDailyFailure;

    public LaborDistributionData() {
    }

    public Double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public LaborDistributionData(EquipmentSubType subType,
                                 Long formationId,
                                 String formationName,
                                 Long equipmentId,
                                 String equipmentName,
                                 Integer laborInput,
                                 Long intervalId,
                                 Double count,
                                 Double avgLaborInput,
                                 Double avgDailyFailure) {
        this.compositeKey = new CompositeKey(subType, formationId, equipmentId);
        this.formationName = formationName;
        this.equipmentName = equipmentName;
        this.laborInput = laborInput;
        this.intervalId = intervalId;
        this.count = count;
        this.avgLaborInput = avgLaborInput;
        this.avgDailyFailure = avgDailyFailure;
    }

    public Integer getLaborInput() {
        return laborInput;
    }

    public String getFormationName() {
        return formationName;
    }

    public CompositeKey getCompositeKey() {
        return compositeKey;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public Long getIntervalId() {
        return intervalId;
    }

    public Double getCount() {
        return count;
    }

    public Double getAvgLaborInput() {
        return avgLaborInput;
    }

    public static class CompositeKey {
        private final EquipmentSubType subType;
        private final Long formationId;
        private final Long equipmentId;

        public CompositeKey(EquipmentSubType subType, Long formationId, Long equipmentId) {
            this.subType = subType;
            this.formationId = formationId;
            this.equipmentId = equipmentId;
        }

        public EquipmentSubType getSubType() {
            return subType;
        }


        public Long getFormationId() {
            return formationId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompositeKey that = (CompositeKey) o;
            return Objects.equals(subType, that.subType) &&
                    Objects.equals(formationId, that.formationId) &&
                    Objects.equals(equipmentId, that.equipmentId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(subType, formationId, equipmentId);
        }

        public Long getEquipmentId() {
            return equipmentId;
        }
    }
}