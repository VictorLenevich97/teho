package va.rit.teho.entity.repairdivision;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RepairDivisionUnitPK implements Serializable {

    @Column(name = "repair_division_unit_id")
    private Long repairDivisionUnitId;
    @Column(name = "equipment_sub_type_id")
    private Long equipmentSubTypeId;
    @Column(name = "session_id")
    private UUID sessionId;

    public RepairDivisionUnitPK() {
    }

    public RepairDivisionUnitPK(Long repairDivisionUnitId, Long equipmentSubTypeId, UUID sessionId) {
        this.repairDivisionUnitId = repairDivisionUnitId;
        this.equipmentSubTypeId = equipmentSubTypeId;
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairDivisionUnitPK that = (RepairDivisionUnitPK) o;
        return Objects.equals(repairDivisionUnitId, that.repairDivisionUnitId) &&
                Objects.equals(equipmentSubTypeId, that.equipmentSubTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairDivisionUnitId, equipmentSubTypeId, sessionId);
    }

    public Long getRepairDivisionUnitId() {
        return repairDivisionUnitId;
    }

    public Long getEquipmentSubTypeId() {
        return equipmentSubTypeId;
    }

    public RepairDivisionUnitPK copy(UUID sessionId) {
        return new RepairDivisionUnitPK(getRepairDivisionUnitId(), getEquipmentSubTypeId(), sessionId);
    }
}
