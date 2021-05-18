package va.rit.teho.entity.repairformation;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RepairFormationUnitRepairCapabilityPK implements Serializable {

    @Column(name = "repair_formation_unit_id")
    private Long repairFormationUnitId;

    @Column(name = "equipment_id")
    private Long equipmentId;

    @Column(name = "session_id")
    private UUID sessionId;

    public RepairFormationUnitRepairCapabilityPK() {
    }

    public RepairFormationUnitRepairCapabilityPK(Long repairFormationUnitId,
                                                 Long equipmentId,
                                                 UUID sessionId) {
        this.repairFormationUnitId = repairFormationUnitId;
        this.equipmentId = equipmentId;
        this.sessionId = sessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairFormationUnitId, equipmentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairFormationUnitRepairCapabilityPK that = (RepairFormationUnitRepairCapabilityPK) o;
        return Objects.equals(repairFormationUnitId, that.repairFormationUnitId) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    public Long getRepairFormationUnitId() {
        return repairFormationUnitId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public RepairFormationUnitRepairCapabilityPK copy(UUID sessionId) {
        return new RepairFormationUnitRepairCapabilityPK(getRepairFormationUnitId(),
                                                         getEquipmentId(),
                                                         sessionId);
    }
}
