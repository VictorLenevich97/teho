package va.rit.teho.dto;

import va.rit.teho.entity.EquipmentLaborInputDistribution;

import java.util.List;
import java.util.stream.Collectors;

public class LaborInputDistributionDTO {
    private final EquipmentTypeDTO type;

    private final List<SubTypeDistributionDTO> subTypeDistribution;

    public EquipmentTypeDTO getType() {
        return type;
    }

    public List<SubTypeDistributionDTO> getSubTypeDistribution() {
        return subTypeDistribution;
    }

    public LaborInputDistributionDTO(EquipmentTypeDTO type,
                                     List<SubTypeDistributionDTO> subTypeDistribution) {
        this.type = type;
        this.subTypeDistribution = subTypeDistribution;
    }

    public static class SubTypeDistributionDTO {
        private final EquipmentSubTypeDTO subType;

        public EquipmentSubTypeDTO getSubType() {
            return subType;
        }

        public List<EquipmentLaborInputDistributionDTO> getEquipmentDistribution() {
            return equipmentDistribution;
        }

        public SubTypeDistributionDTO(EquipmentSubTypeDTO subType,
                                      List<EquipmentLaborInputDistributionDTO> equipmentDistribution) {
            this.subType = subType;
            this.equipmentDistribution = equipmentDistribution;
        }

        private final List<EquipmentLaborInputDistributionDTO> equipmentDistribution;
    }

    public static class EquipmentLaborInputDistributionDTO {
        private final String baseName;
        private final String equipmentName;
        private final double avgDailyFailure;
        private final int standardLaborInput;
        private final List<IntervalWithCountAndLaborInputDTO> countAndLaborInputs;
        private final double totalLaborInput;

        public String getBaseName() {
            return baseName;
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

        public List<IntervalWithCountAndLaborInputDTO> getCountAndLaborInputs() {
            return countAndLaborInputs;
        }

        public double getTotalLaborInput() {
            return totalLaborInput;
        }

        public EquipmentLaborInputDistributionDTO(String baseName,
                                                  String equipmentName,
                                                  double avgDailyFailure,
                                                  int standardLaborInput,
                                                  List<IntervalWithCountAndLaborInputDTO> countAndLaborInputs,
                                                  double totalLaborInput) {
            this.baseName = baseName;
            this.equipmentName = equipmentName;
            this.avgDailyFailure = avgDailyFailure;
            this.standardLaborInput = standardLaborInput;
            this.countAndLaborInputs = countAndLaborInputs;
            this.totalLaborInput = totalLaborInput;
        }

        public static EquipmentLaborInputDistributionDTO from(EquipmentLaborInputDistribution entity) {
            return new EquipmentLaborInputDistributionDTO(
                    entity.getBaseName(),
                    entity.getEquipmentName(),
                    entity.getAvgDailyFailure(),
                    entity.getStandardLaborInput(),
                    entity
                            .getIntervalCountAndLaborInputMap()
                            .entrySet()
                            .stream()
                            .map(laborInputEntry -> new IntervalWithCountAndLaborInputDTO(laborInputEntry.getKey(),
                                                                                          laborInputEntry
                                                                                                  .getValue()
                                                                                                  .getCount(),
                                                                                          laborInputEntry
                                                                                                  .getValue()
                                                                                                  .getLaborInput()))
                            .collect(Collectors.toList()),
                    entity.getTotalRepairComplexity());
        }
    }

    public static class IntervalWithCountAndLaborInputDTO {
        private final Long key;
        private final Double count;
        private final Double laborInput;

        public Long getKey() {
            return key;
        }

        public Double getCount() {
            return count;
        }

        public Double getLaborInput() {
            return laborInput;
        }

        public IntervalWithCountAndLaborInputDTO(Long key, Double count, Double laborInput) {
            this.key = key;
            this.count = count;
            this.laborInput = laborInput;
        }
    }
}
