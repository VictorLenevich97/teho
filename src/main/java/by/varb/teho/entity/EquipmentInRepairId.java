package by.varb.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EquipmentInRepairId implements Serializable {
    @Column(name = "base_id")
    private Long baseId;
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Column(name = "workhours_distribution_interval_id")
    private Long workhoursDistributionIntervalId;

    public EquipmentInRepairId(Long baseId, Long equipmentId, Long workhoursDistributionIntervalId) {
        this.baseId = baseId;
        this.equipmentId = equipmentId;
        this.workhoursDistributionIntervalId = workhoursDistributionIntervalId;
    }

    public EquipmentInRepairId() {
    }


}
