package va.rit.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EquipmentPerRepairStationWithRepairType implements Serializable {

    @Column(name = "repair_station_id")
    private Long repairStationId;
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "repair_type_id")
    private Long repairTypeId;

    public EquipmentPerRepairStationWithRepairType() {
    }

    public EquipmentPerRepairStationWithRepairType(Long repairStationId,
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
        EquipmentPerRepairStationWithRepairType that = (EquipmentPerRepairStationWithRepairType) o;
        return Objects.equals(repairStationId, that.repairStationId) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(repairTypeId, that.repairTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }
}
