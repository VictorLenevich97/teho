package va.rit.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EquipmentLaborInputPerTypeAmount implements Serializable {
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Column(name = "repair_type_id")
    private Long repairTypeId;
}
