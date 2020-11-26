package va.rit.teho.entity.repairdivision;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RepairDivisionUnitRepairCapabilityPK implements Serializable {

    @Column(name = "repair_division_unit_id")
    private Long repairDivisionUnitId;
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Column(name = "session_id")
    private UUID sessionId;
    @Column(name = "repair_type_id")
    private Long repairTypeId;

    public RepairDivisionUnitRepairCapabilityPK() {
    }

    public RepairDivisionUnitRepairCapabilityPK(Long repairDivisionUnitId,
                                                Long equipmentId,
                                                Long repairTypeId,
                                                UUID sessionId) {
        this.repairDivisionUnitId = repairDivisionUnitId;
        this.equipmentId = equipmentId;
        this.repairTypeId = repairTypeId;
        this.sessionId = sessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairDivisionUnitId, equipmentId, repairTypeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairDivisionUnitRepairCapabilityPK that = (RepairDivisionUnitRepairCapabilityPK) o;
        return Objects.equals(repairDivisionUnitId, that.repairDivisionUnitId) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(repairTypeId, that.repairTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Long getRepairDivisionUnitId() {
        return repairDivisionUnitId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Long getRepairTypeId() {
        return repairTypeId;
    }

    public RepairDivisionUnitRepairCapabilityPK copy(UUID sessionId) {
        return new RepairDivisionUnitRepairCapabilityPK(getRepairDivisionUnitId(),
                                                        getEquipmentId(),
                                                        getRepairTypeId(),
                                                        sessionId);
    }
}
