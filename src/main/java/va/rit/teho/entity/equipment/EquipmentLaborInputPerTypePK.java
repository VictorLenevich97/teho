package va.rit.teho.entity.equipment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EquipmentLaborInputPerTypePK implements Serializable {
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Column(name = "repair_type_id")
    private Long repairTypeId;

    public EquipmentLaborInputPerTypePK() {
    }

    public EquipmentLaborInputPerTypePK(Long equipmentId, Long repairTypeId) {
        this.equipmentId = equipmentId;
        this.repairTypeId = repairTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentLaborInputPerTypePK that = (EquipmentLaborInputPerTypePK) o;
        return Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(repairTypeId, that.repairTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId, repairTypeId);
    }
}
