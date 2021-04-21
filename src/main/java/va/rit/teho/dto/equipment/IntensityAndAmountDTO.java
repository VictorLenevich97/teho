package va.rit.teho.dto.equipment;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntensityAndAmountDTO {
    private List<Long> equipmentIds;
    private List<IntensityPerRepairTypeAndStageDTO> intensities;

    @Positive
    @Max(10000)
    private int amount;

    public IntensityAndAmountDTO(List<IntensityPerRepairTypeAndStageDTO> intensities, int amount) {
        this.intensities = intensities;
        this.amount = amount;
    }

    public IntensityAndAmountDTO() {
    }

    public List<IntensityPerRepairTypeAndStageDTO> getIntensities() {
        return intensities;
    }

    public void setIntensities(List<IntensityPerRepairTypeAndStageDTO> intensities) {
        this.intensities = intensities;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
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
