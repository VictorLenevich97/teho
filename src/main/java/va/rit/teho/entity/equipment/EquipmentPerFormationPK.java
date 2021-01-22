package va.rit.teho.entity.equipment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EquipmentPerFormationPK implements Serializable {

    @Column(name = "formation_id")
    private Long formationId;

    @Column(name = "equipment_id")
    private Long equipmentId;

    public EquipmentPerFormationPK() {
    }

    public EquipmentPerFormationPK(Long formationId, Long equipmentId) {
        this.formationId = formationId;
        this.equipmentId = equipmentId;
    }

    public Long getFormationId() {
        return formationId;
    }

    public void setFormationId(Long formationId) {
        this.formationId = formationId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPerFormationPK that = (EquipmentPerFormationPK) o;
        return Objects.equals(formationId, that.formationId) &&
                Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formationId, equipmentId);
    }
}
