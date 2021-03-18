package va.rit.teho.entity.intensity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IntensityPK implements Serializable {

    @Column(name = "operation_id")
    private Long operationId;

    @Column(name = "equipment_id")
    private Long equipmentId;

    @Column(name = "stage_id")
    private Long stageId;

    @Column(name = "repair_type_id")
    private Long repairTypeId;

    public IntensityPK() {
    }

    public IntensityPK(Long operationId, Long equipmentId, Long stageId, Long repairTypeId) {
        this.operationId = operationId;
        this.equipmentId = equipmentId;
        this.stageId = stageId;
        this.repairTypeId = repairTypeId;
    }

    public Long getOperationId() {
        return operationId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public Long getStageId() {
        return stageId;
    }

    public Long getRepairTypeId() {
        return repairTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntensityPK that = (IntensityPK) o;
        return Objects.equals(operationId, that.operationId) && Objects.equals(equipmentId, that.equipmentId) && Objects.equals(stageId, that.stageId) && Objects.equals(repairTypeId, that.repairTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, equipmentId, stageId, repairTypeId);
    }
}
