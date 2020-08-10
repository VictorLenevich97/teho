package by.varb.teho.entity;

import javax.persistence.*;

@Entity
public class EquipmentPerBase {

    @EmbeddedId
    EquipmentPerBaseAmount id;

    @ManyToOne
    @MapsId("base_id")
    @JoinColumn(name = "base_id")
    Base base;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    //Интенсивность выхода в ремонт, 0-100 (%)
    int intensity;
    int amount;

    public EquipmentPerBase() {
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public EquipmentPerBase(Base base, Equipment equipment, int intensity, int amount) {
        this.id = new EquipmentPerBaseAmount(base.getId(), equipment.getId());
        this.base = base;
        this.equipment = equipment;
        this.intensity = intensity;
        this.amount = amount;
    }

    public EquipmentPerBaseAmount getId() {
        return id;
    }

    public void setId(EquipmentPerBaseAmount id) {
        this.id = id;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
