package va.rit.teho.entity.equipment;

import va.rit.teho.entity.base.Base;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class EquipmentPerBase {

    @EmbeddedId
    EquipmentPerBasePK id;

    @ManyToOne
    @MapsId("base_id")
    @JoinColumn(name = "base_id")
    Base base;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    int amount;

    public EquipmentPerBase() {
    }

    public EquipmentPerBase(Long baseId, Long equipmentId, int amount) {
        this.id = new EquipmentPerBasePK(baseId, equipmentId);
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPerBase that = (EquipmentPerBase) o;
        return amount == that.amount &&
                Objects.equals(id, that.id) &&
                Objects.equals(base, that.base) &&
                Objects.equals(equipment, that.equipment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, base, equipment, amount);
    }

    public EquipmentPerBasePK getId() {
        return id;
    }

    public void setId(EquipmentPerBasePK id) {
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
