package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "equipment_rfu_distribution")
public class EquipmentRFUDistribution {

    @EmbeddedId
    private EquipmentRFUDistributionPK equipmentRFUDistributionPK;

    @ManyToOne
    @MapsId("formation_id")
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @MapsId("repair_formation_unit_id")
    @JoinColumn(name = "repair_formation_unit_id")
    private RepairFormationUnit repairFormationUnit;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    private TehoSession tehoSession;

    @ManyToOne
    @MapsId("interval_id")
    @JoinColumn(name = "interval_id")
    private WorkhoursDistributionInterval workhoursDistributionInterval;

    private Double repairing;

    private Double unable;

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

    public EquipmentRFUDistribution copy(UUID newSessionId) {
        return new EquipmentRFUDistribution(getEquipmentRFUDistributionPK().copy(newSessionId), repairing, unable);
    }

}
