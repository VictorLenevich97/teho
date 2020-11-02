package va.rit.teho.entity.repairstation;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RepairStationRepairCapabilityPK implements Serializable {

    @Column(name = "repair_station_id")
    private Long repairStationId;
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Column(name = "session_id")
    private UUID sessionId;
    @Column(name = "repair_type_id")
    private Long repairTypeId;

    public RepairStationRepairCapabilityPK() {
    }

    public RepairStationRepairCapabilityPK(Long repairStationId,
                                           Long equipmentId,
                                           Long repairTypeId,
                                           UUID sessionId) {
        this.repairStationId = repairStationId;
        this.equipmentId = equipmentId;
        this.repairTypeId = repairTypeId;
        this.sessionId = sessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairStationId, equipmentId, repairTypeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairStationRepairCapabilityPK that = (RepairStationRepairCapabilityPK) o;
        return Objects.equals(repairStationId, that.repairStationId) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(repairTypeId, that.repairTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Long getRepairStationId() {
        return repairStationId;
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

    public RepairStationRepairCapabilityPK copy(UUID sessionId) {
        return new RepairStationRepairCapabilityPK(getRepairStationId(),
                                                   getEquipmentId(),
                                                   getRepairTypeId(),
                                                   sessionId);
    }
}
