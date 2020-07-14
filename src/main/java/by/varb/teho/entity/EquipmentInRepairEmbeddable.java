package by.varb.teho.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EquipmentInRepairEmbeddable implements Serializable {

    @Column(name = "repair_station_id")
    private Long repairStationId;

    @Column(name = "equipment_id")
    private Long equipmentId;

    @Column(name = "workhours_distribution_interval_id")
    private Long workhoursDistributionIntervalId;

}
