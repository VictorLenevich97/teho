package va.rit.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EquipmentSubTypePerRepairStation implements Serializable {

    @Column(name = "repair_station_id")
    private Long repairStationId;
    @Column(name = "equipment_sub_type_id")
    private Long equipmentSubTypeId;

    public EquipmentSubTypePerRepairStation() {
    }

    public EquipmentSubTypePerRepairStation(Long repairStationId, Long equipmentSubTypeId) {
        this.repairStationId = repairStationId;
        this.equipmentSubTypeId = equipmentSubTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentSubTypePerRepairStation that = (EquipmentSubTypePerRepairStation) o;
        return Objects.equals(repairStationId, that.repairStationId) &&
                Objects.equals(equipmentSubTypeId, that.equipmentSubTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairStationId, equipmentSubTypeId);
    }

    public Long getRepairStationId() {
        return repairStationId;
    }

    public Long getEquipmentSubTypeId() {
        return equipmentSubTypeId;
    }
}
