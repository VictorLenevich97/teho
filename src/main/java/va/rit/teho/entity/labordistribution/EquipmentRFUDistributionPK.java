package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.EquipmentPerFormation;

import javax.persistence.*;
import java.io.Serializable;
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
}
