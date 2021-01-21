package va.rit.teho.dto.equipment;

import java.util.List;

public class IntensityAndAmountDTO {
    private List<IntensityPerRepairTypeAndStageDTO> intensities;
    private int amount;

    public IntensityAndAmountDTO(List<IntensityPerRepairTypeAndStageDTO> intensities, int amount) {
        this.intensities = intensities;
        this.amount = amount;
    }

    public IntensityAndAmountDTO() {
    }

    public void setIntensities(List<IntensityPerRepairTypeAndStageDTO> intensities) {
        this.intensities = intensities;
    }

    public List<IntensityPerRepairTypeAndStageDTO> getIntensities() {
        return intensities;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public static final class IntensityPerRepairTypeAndStageDTO {
        private final Long repairTypeId;
        private final Long stageId;
        private final Integer intensity;

        public IntensityPerRepairTypeAndStageDTO(Long repairTypeId, Long stageId, Integer intensity) {
            this.repairTypeId = repairTypeId;
            this.stageId = stageId;
            this.intensity = intensity;
        }

        public Long getRepairTypeId() {
            return repairTypeId;
        }

        public Long getStageId() {
            return stageId;
        }

        public Integer getIntensity() {
            return intensity;
        }
    }
}
