package by.varb.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EquipmentInRepairEmbeddable implements Serializable {

    @Column(name = "base_id")
    private Long baseId;

    @Column(name = "equipment_id")
    private Long equipmentId;

    @Column(name = "workhours_distribution_interval_id")
    private Long workhoursDistributionIntervalId;

}
