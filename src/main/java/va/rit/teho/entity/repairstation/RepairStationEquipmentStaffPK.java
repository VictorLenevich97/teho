package va.rit.teho.entity.repairstation;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RepairStationEquipmentStaffPK implements Serializable {

    @Column(name = "repair_station_id")
    private Long repairStationId;
    @Column(name = "equipment_sub_type_id")
    private Long equipmentSubTypeId;
    @Column(name = "session_id")
    private UUID sessionId;

    public RepairStationEquipmentStaffPK() {
    }

    public RepairStationEquipmentStaffPK(Long repairStationId, Long equipmentSubTypeId, UUID sessionId) {
        this.repairStationId = repairStationId;
        this.equipmentSubTypeId = equipmentSubTypeId;
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairStationEquipmentStaffPK that = (RepairStationEquipmentStaffPK) o;
        return Objects.equals(repairStationId, that.repairStationId) &&
                Objects.equals(equipmentSubTypeId, that.equipmentSubTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairStationId, equipmentSubTypeId, sessionId);
    }

    public Long getRepairStationId() {
        return repairStationId;
    }

    public Long getEquipmentSubTypeId() {
        return equipmentSubTypeId;
    }

    public RepairStationEquipmentStaffPK copy(UUID sessionId) {
        return new RepairStationEquipmentStaffPK(getRepairStationId(), getEquipmentSubTypeId(), sessionId);
    }
}
