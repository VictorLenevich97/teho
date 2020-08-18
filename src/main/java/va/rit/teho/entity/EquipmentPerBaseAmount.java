package va.rit.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

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

}
