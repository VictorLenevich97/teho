package by.varb.teho.entity;

import javax.persistence.Entity;

@Entity
public class RepairStationEquipmentStaff extends AbstractRepairStationEquipmentEntity {

    int totalStaff;
    int availableStaff;

    public RepairStationEquipmentStaff() {
        //Пустой конструктор для автоматической инициализации
    }

    public int getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }

    public int getAvailableStaff() {
        return availableStaff;
    }

    public void setAvailableStaff(int availableStaff) {
        this.availableStaff = availableStaff;
    }
}
