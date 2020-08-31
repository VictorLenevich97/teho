package va.rit.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EquipmentPerBaseAmount implements Serializable {

    @Column(name = "base_id")
    Long baseId;
    @Column(name = "equipment_id")
    Long equipmentId;

    public EquipmentPerBaseAmount() {
    }

    public EquipmentPerBaseAmount(Long baseId, Long equipmentId) {
        this.baseId = baseId;
        this.equipmentId = equipmentId;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
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
        EquipmentPerBaseAmount that = (EquipmentPerBaseAmount) o;
        return Objects.equals(baseId, that.baseId) &&
                Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseId, equipmentId);
    }
}
