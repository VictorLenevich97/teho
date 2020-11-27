package va.rit.teho.entity.labordistribution;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class LaborDistributionPK implements Serializable {
    @Column(name = "base_id")
    private Long baseId;
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Column(name = "workhours_distribution_interval_id")
    private Long workhoursDistributionIntervalId;
    @Column(name = "stage_id")
    private Long stageId;
    @Column(name = "repair_type_id")
    private Long repairTypeId;
    @Column(name = "session_id")
    private UUID sessionId;

    public Long getRepairTypeId() {
        return repairTypeId;
    }

    public LaborDistributionPK(Long baseId,
                               Long equipmentId,
                               Long workhoursDistributionIntervalId,
                               Long stageId,
                               Long repairTypeId,
                               UUID sessionId) {
        this.baseId = baseId;
        this.equipmentId = equipmentId;
        this.workhoursDistributionIntervalId = workhoursDistributionIntervalId;
        this.stageId = stageId;
        this.repairTypeId = repairTypeId;
        this.sessionId = sessionId;
    }

    public LaborDistributionPK() {
    }

    public Long getBaseId() {
        return baseId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public LaborDistributionPK copy(UUID sessionId) {
        return new LaborDistributionPK(baseId, equipmentId, workhoursDistributionIntervalId, stageId, repairTypeId, sessionId);
    }

    public Long getWorkhoursDistributionIntervalId() {
        return workhoursDistributionIntervalId;
    }

    public Long getStageId() {
        return stageId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaborDistributionPK that = (LaborDistributionPK) o;
        return Objects.equals(baseId, that.baseId) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(workhoursDistributionIntervalId, that.workhoursDistributionIntervalId) &&
                Objects.equals(stageId, that.stageId) &&
                Objects.equals(repairTypeId, that.repairTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseId, equipmentId, workhoursDistributionIntervalId, stageId, repairTypeId, sessionId);
    }
}
