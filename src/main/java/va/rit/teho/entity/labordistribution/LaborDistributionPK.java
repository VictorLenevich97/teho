package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.EquipmentPerFormation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class LaborDistributionPK implements Serializable {

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "equipment_id"),
            @JoinColumn(name = "formation_id")
    })
    private EquipmentPerFormation equipmentPerFormation;

    @Column(name = "workhours_distribution_interval_id")
    private Long workhoursDistributionIntervalId;

    @Column(name = "stage_id")
    private Long stageId;

    @Column(name = "repair_type_id")
    private Long repairTypeId;

    @Column(name = "session_id")
    private UUID sessionId;

    public LaborDistributionPK(Long formationId,
                               Long equipmentId,
                               Long workhoursDistributionIntervalId,
                               Long stageId,
                               Long repairTypeId,
                               UUID sessionId) {
        this.equipmentPerFormation = new EquipmentPerFormation(formationId, equipmentId);
        this.workhoursDistributionIntervalId = workhoursDistributionIntervalId;
        this.stageId = stageId;
        this.repairTypeId = repairTypeId;
        this.sessionId = sessionId;
    }

    public LaborDistributionPK() {
    }

    public Long getRepairTypeId() {
        return repairTypeId;
    }

    public Long getFormationId() {
        return equipmentPerFormation.getId().getFormationId();
    }

    public Long getEquipmentId() {
        return equipmentPerFormation.getId().getEquipmentId();
    }

    public LaborDistributionPK copy(UUID sessionId) {
        return new LaborDistributionPK(getFormationId(),
                                       getEquipmentId(),
                                       workhoursDistributionIntervalId,
                                       stageId,
                                       repairTypeId,
                                       sessionId);
    }

    public Long getWorkhoursDistributionIntervalId() {
        return workhoursDistributionIntervalId;
    }

    public Long getStageId() {
        return stageId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaborDistributionPK that = (LaborDistributionPK) o;
        return Objects.equals(equipmentPerFormation, that.equipmentPerFormation) &&
                Objects.equals(workhoursDistributionIntervalId, that.workhoursDistributionIntervalId) &&
                Objects.equals(stageId, that.stageId) &&
                Objects.equals(repairTypeId, that.repairTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerFormation, workhoursDistributionIntervalId, stageId, repairTypeId, sessionId);
    }
}
