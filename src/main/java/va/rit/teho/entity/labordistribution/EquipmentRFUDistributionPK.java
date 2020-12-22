package va.rit.teho.entity.labordistribution;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class EquipmentRFUDistributionPK implements Serializable {

    @Column(name = "formation_id")
    private Long formationId;

    @Column(name = "equipment_id")
    private Long equipmentId;

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
        this.formationId = formationId;
        this.equipmentId = equipmentId;
        this.repairFormationUnitId = repairFormationUnitId;
        this.intervalId = intervalId;
        this.sessionId = sessionId;
    }

    public Long getIntervalId() {
        return intervalId;
    }

    public Long getFormationId() {
        return formationId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public Long getRepairFormationUnitId() {
        return repairFormationUnitId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public EquipmentRFUDistributionPK copy(UUID newSessionId) {
        return new EquipmentRFUDistributionPK(formationId, equipmentId, repairFormationUnitId, intervalId, newSessionId);
    }
}
