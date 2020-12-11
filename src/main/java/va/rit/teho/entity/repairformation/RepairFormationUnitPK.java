package va.rit.teho.entity.repairformation;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RepairFormationUnitPK implements Serializable {

    @Column(name = "repair_formation_unit_id")
    private Long repairFormationUnitId;
    @Column(name = "equipment_sub_type_id")
    private Long equipmentSubTypeId;
    @Column(name = "session_id")
    private UUID sessionId;

    public RepairFormationUnitPK() {
    }

    public RepairFormationUnitPK(Long repairFormationUnitId, Long equipmentSubTypeId, UUID sessionId) {
        this.repairFormationUnitId = repairFormationUnitId;
        this.equipmentSubTypeId = equipmentSubTypeId;
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairFormationUnitPK that = (RepairFormationUnitPK) o;
        return Objects.equals(repairFormationUnitId, that.repairFormationUnitId) &&
                Objects.equals(equipmentSubTypeId, that.equipmentSubTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairFormationUnitId, equipmentSubTypeId, sessionId);
    }

    public Long getRepairFormationUnitId() {
        return repairFormationUnitId;
    }

    public Long getEquipmentSubTypeId() {
        return equipmentSubTypeId;
    }

    public RepairFormationUnitPK copy(UUID sessionId) {
        return new RepairFormationUnitPK(getRepairFormationUnitId(), getEquipmentSubTypeId(), sessionId);
    }
}
