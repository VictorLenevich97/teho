package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.EquipmentPerFormation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EquipmentRFUDistributionPK implements Serializable {

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "equipment_id"),
            @JoinColumn(name = "formation_id")
    })
    private EquipmentPerFormation equipmentPerFormation;

    @Column(name = "repair_formation_unit_id")
    private Long repairFormationUnitId;

    @Column(name = "interval_id")
    private Long intervalId;

    @Column(name = "session_id")
    private UUID sessionId;


    public EquipmentRFUDistributionPK() {
    }

    public EquipmentRFUDistributionPK(Long formationId,
                                      Long equipmentId,
                                      Long repairFormationUnitId,
                                      Long intervalId,
                                      UUID sessionId) {
        this.equipmentPerFormation = new EquipmentPerFormation(formationId, equipmentId);
        this.repairFormationUnitId = repairFormationUnitId;
        this.intervalId = intervalId;
        this.sessionId = sessionId;
    }

    public Long getIntervalId() {
        return intervalId;
    }

    public Long getFormationId() {
        return equipmentPerFormation.getId().getFormationId();
    }

    public Long getEquipmentId() {
        return equipmentPerFormation.getId().getEquipmentId();
    }

    public Long getRepairFormationUnitId() {
        return repairFormationUnitId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public EquipmentRFUDistributionPK copy(UUID newSessionId) {
        return new EquipmentRFUDistributionPK(getFormationId(),
                                              getEquipmentId(),
                                              repairFormationUnitId,
                                              intervalId,
                                              newSessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerFormation, repairFormationUnitId, intervalId, sessionId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentRFUDistributionPK that = (EquipmentRFUDistributionPK) o;
        return Objects.equals(equipmentPerFormation, that.equipmentPerFormation) && Objects.equals(
                repairFormationUnitId,
                that.repairFormationUnitId) && Objects.equals(intervalId,
                                                              that.intervalId) && Objects.equals(
                sessionId,
                that.sessionId);
    }
}
