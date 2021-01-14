package va.rit.teho.entity.equipment;

import va.rit.teho.entity.formation.Formation;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class EquipmentPerFormation {

    @EmbeddedId
    EquipmentPerFormationPK id;

    @ManyToOne
    @MapsId("formation_id")
    @JoinColumn(name = "formation_id")
    Formation formation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    int amount;

    public EquipmentPerFormation() {
    }

    public EquipmentPerFormation(Long formationId, Long equipmentId, Long amount) {
        this.id = new EquipmentPerFormationPK(formationId, equipmentId);
        this.amount = amount.intValue();
    }

    public EquipmentPerFormation(Equipment equipment, Formation formation, Long amount) {
        this.id = new EquipmentPerFormationPK(formation.getId(), equipment.getId());
        this.formation = formation;
        this.equipment = equipment;
        this.amount = amount.intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPerFormation that = (EquipmentPerFormation) o;
        return amount == that.amount &&
                Objects.equals(id, that.id) &&
                Objects.equals(formation, that.formation) &&
                Objects.equals(equipment, that.equipment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, formation, equipment, amount);
    }

    public EquipmentPerFormationPK getId() {
        return id;
    }

    public void setId(EquipmentPerFormationPK id) {
        this.id = id;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
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
