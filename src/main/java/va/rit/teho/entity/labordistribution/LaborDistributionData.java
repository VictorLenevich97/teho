package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.EquipmentSubType;

import java.util.Objects;

public class LaborDistributionData {

    private CompositeKey compositeKey;
    private String baseName;
    private String equipmentName;
    private Integer laborInput;
    private Long intervalId;
    private Double count;
    private Double avgLaborInput;

    public LaborDistributionData() {
    }

    public LaborDistributionData(EquipmentSubType subType,
                                 String baseName,
                                 Long equipmentId,
                                 String equipmentName,
                                 Integer laborInput,
                                 Long intervalId,
                                 Double count,
                                 Double avgLaborInput) {
        this.compositeKey = new CompositeKey(subType, equipmentId);
        this.baseName = baseName;
        this.equipmentName = equipmentName;
        this.laborInput = laborInput;
        this.intervalId = intervalId;
        this.count = count;
        this.avgLaborInput = avgLaborInput;
    }

    public Integer getLaborInput() {
        return laborInput;
    }

    public String getBaseName() {
        return baseName;
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
        private final Long equipmentId;

        public CompositeKey(EquipmentSubType subType, Long equipmentId) {
            this.subType = subType;
            this.equipmentId = equipmentId;
        }

        public EquipmentSubType getSubType() {
            return subType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CompositeKey tempKey = (CompositeKey) o;
            return Objects.equals(subType, tempKey.subType) &&
                    Objects.equals(equipmentId, tempKey.equipmentId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(subType, equipmentId);
        }

        public Long getEquipmentId() {
            return equipmentId;
        }
    }
}