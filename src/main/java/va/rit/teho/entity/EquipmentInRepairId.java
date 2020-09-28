package va.rit.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EquipmentInRepairId implements Serializable {
    @Column(name = "base_id")
    private Long baseId;
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Column(name = "workhours_distribution_interval_id")
    private Long workhoursDistributionIntervalId;
    @Column(name = "session_id")
    private UUID sessionId;

    public EquipmentInRepairId(Long baseId, Long equipmentId, Long workhoursDistributionIntervalId, UUID sessionId) {
        this.baseId = baseId;
        this.equipmentId = equipmentId;
        this.workhoursDistributionIntervalId = workhoursDistributionIntervalId;
        this.sessionId = sessionId;
    }

    public EquipmentInRepairId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentInRepairId that = (EquipmentInRepairId) o;
        return Objects.equals(baseId, that.baseId) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(workhoursDistributionIntervalId, that.workhoursDistributionIntervalId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseId, equipmentId, workhoursDistributionIntervalId, sessionId);
    }
}
