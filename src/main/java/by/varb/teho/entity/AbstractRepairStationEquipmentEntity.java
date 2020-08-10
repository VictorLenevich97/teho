package by.varb.teho.entity;

import javax.persistence.*;

@Entity
@Inheritance
public abstract class AbstractRepairStationEquipmentEntity {
    @EmbeddedId
    EquipmentPerRepairStation equipmentPerRepairStation;

    @ManyToOne
    @MapsId("repair_station_id")
    @JoinColumn(name = "repair_station_id")
    RepairStation repairStation;
    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    public AbstractRepairStationEquipmentEntity() {
        //Пустой конструктор для автоматической инициализации
    }

    public AbstractRepairStationEquipmentEntity(EquipmentPerRepairStation equipmentPerRepairStation, RepairStation repairStation, Equipment equipment) {
        this.equipmentPerRepairStation = equipmentPerRepairStation;
        this.repairStation = repairStation;
        this.equipment = equipment;
    }

    public EquipmentPerRepairStation getEquipmentPerRepairStation() {
        return equipmentPerRepairStation;
    }

    public void setEquipmentPerRepairStation(EquipmentPerRepairStation equipmentPerRepairStation) {
        this.equipmentPerRepairStation = equipmentPerRepairStation;
    }

    public RepairStation getRepairStation() {
        return repairStation;
    }

    public void setRepairStation(RepairStation repairStation) {
        this.repairStation = repairStation;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }
}
