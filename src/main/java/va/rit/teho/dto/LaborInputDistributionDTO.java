package va.rit.teho.dto;

import va.rit.teho.dto.equipment.EquipmentSubTypeDTO;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
import va.rit.teho.entity.EquipmentLaborInputDistribution;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LaborInputDistributionDTO {
    private final EquipmentTypeDTO type;

    private final List<SubTypeDistributionDTO> subTypeDistribution;

    public LaborInputDistributionDTO(EquipmentTypeDTO type,
                                     List<SubTypeDistributionDTO> subTypeDistribution) {
        this.type = type;
        this.subTypeDistribution = subTypeDistribution;
    }

    public EquipmentTypeDTO getType() {
        return type;
    }

    public List<SubTypeDistributionDTO> getSubTypeDistribution() {
        return subTypeDistribution;
    }

    public static class SubTypeDistributionDTO {
        private final EquipmentSubTypeDTO subType;
        private final List<EquipmentLaborInputDistributionDTO> equipmentDistribution;

        public SubTypeDistributionDTO(EquipmentSubTypeDTO subType,
                                      List<EquipmentLaborInputDistributionDTO> equipmentDistribution) {
            this.subType = subType;
            this.equipmentDistribution = equipmentDistribution;
        }

        public EquipmentSubTypeDTO getSubType() {
            return subType;
        }

        public List<EquipmentLaborInputDistributionDTO> getEquipmentDistribution() {
            return equipmentDistribution;
        }
    }

    public static class EquipmentLaborInputDistributionDTO {
        private final String baseName;
        private final String equipmentName;
        private final double avgDailyFailure;
        private final int standardLaborInput;
        private final Map<String, CountAndLaborInputDTO> countAndLaborInputs;
        private final double totalLaborInput;

        public EquipmentLaborInputDistributionDTO(String baseName,
                                                  String equipmentName,
                                                  double avgDailyFailure,
                                                  int standardLaborInput,
                                                  Map<String, CountAndLaborInputDTO> countAndLaborInputs,
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
                            .collect(Collectors.toMap(
                                    e -> e.getKey().toString(),
                                    e -> new CountAndLaborInputDTO(e.getValue().getCount(),
                                                                   e.getValue().getLaborInput()))),
                    entity.getTotalRepairComplexity());
        }

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

        public Map<String, CountAndLaborInputDTO> getCountAndLaborInputs() {
            return countAndLaborInputs;
        }

        public double getTotalLaborInput() {
            return totalLaborInput;
        }
    }

    public static class CountAndLaborInputDTO {
        private final Double count;
        private final Double laborInput;

        public CountAndLaborInputDTO(Double count, Double laborInput) {
            this.count = count;
            this.laborInput = laborInput;
        }

        public Double getCount() {
            return count;
        }

        public Double getLaborInput() {
            return laborInput;
        }
    }
}
