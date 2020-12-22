package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;

@Entity
@Table(name = "equipment_rfu_distribution")
public class EquipmentRFUDistribution {

    @EmbeddedId
    EquipmentRFUDistributionPK equipmentRFUDistributionPK;

    @ManyToOne
    @MapsId("formation_id")
    @JoinColumn(name = "formation_id")
    Formation formation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    @ManyToOne
    @MapsId("repair_formation_unit_id")
    @JoinColumn(name = "repair_formation_unit_id")
    RepairFormationUnit repairFormationUnit;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    TehoSession tehoSession;

    @ManyToOne
    @MapsId("interval_id")
    @JoinColumn(name = "interval_id")
    WorkhoursDistributionInterval workhoursDistributionInterval;

    Double repairing;
    Double unable;

    public EquipmentRFUDistribution() {
    }

    public Double getRepairing() {
        return repairing;
    }

    public Double getUnable() {
        return unable;
    }

    public EquipmentRFUDistribution(EquipmentRFUDistributionPK equipmentRFUDistributionPK,
                                    Double repairing,
                                    Double unable) {
        this.equipmentRFUDistributionPK = equipmentRFUDistributionPK;
        this.repairing = repairing;
        this.unable = unable;
    }

    public EquipmentRFUDistribution(EquipmentRFUDistributionPK equipmentRFUDistributionPK,
                                    Formation formation,
                                    Equipment equipment,
                                    RepairFormationUnit repairFormationUnit,
                                    WorkhoursDistributionInterval interval,
                                    Double repairing,
                                    Double unable) {
        this.equipmentRFUDistributionPK = equipmentRFUDistributionPK;
        this.formation = formation;
        this.equipment = equipment;
        this.repairFormationUnit = repairFormationUnit;
        this.workhoursDistributionInterval = interval;
        this.repairing = repairing;
        this.unable = unable;
    }

    public EquipmentRFUDistributionPK getEquipmentRFUDistributionPK() {
        return equipmentRFUDistributionPK;
    }

    public Formation getFormation() {
        return formation;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public RepairFormationUnit getRepairFormationUnit() {
        return repairFormationUnit;
    }

    public TehoSession getTehoSession() {
        return tehoSession;
    }

    public WorkhoursDistributionInterval getWorkhoursDistributionInterval() {
        return workhoursDistributionInterval;
    }

}
