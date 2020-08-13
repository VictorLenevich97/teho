package by.varb.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

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

}
