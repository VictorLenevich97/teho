package by.varb.teho.entity;

import javax.persistence.*;

@Entity
public class CalculatedRepairCapabilitesPerDay extends AbstractRepairStationEquipmentEntity {
    double capability;

    public CalculatedRepairCapabilitesPerDay() {
        //Пустой конструктор для автоматической инициализации
    }

    public CalculatedRepairCapabilitesPerDay(EquipmentPerRepairStation equipmentPerRepairStation, RepairStation repairStation, Equipment equipment, double capability) {
        super(equipmentPerRepairStation, repairStation, equipment);
        this.capability = capability;
    }

    public double getCapability() {
        return capability;
    }

    public void setCapability(double capability) {
        this.capability = capability;
    }
}