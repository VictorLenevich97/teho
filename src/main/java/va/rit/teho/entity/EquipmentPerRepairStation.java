package va.rit.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EquipmentPerRepairStation implements Serializable {

    @Column(name = "repair_station_id")
    private Long repairStationId;
    @Column(name = "equipment_id")
    private Long equipmentId;

    public EquipmentPerRepairStation() {
    }

    public EquipmentPerRepairStation(Long repairStationId, Long equipmentId) {
        this.repairStationId = repairStationId;
        this.equipmentId = equipmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPerRepairStation that = (EquipmentPerRepairStation) o;
        return Objects.equals(repairStationId, that.repairStationId) &&
                Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairStationId, equipmentId);
    }
}
