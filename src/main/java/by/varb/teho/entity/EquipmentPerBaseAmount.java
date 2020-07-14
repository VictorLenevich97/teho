package by.varb.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
class EquipmentPerBaseAmount implements Serializable {

    @Column(name = "base_id")
    Long baseId;

    @Column(name = "equipment_id")
    Long equipmentId;

}
