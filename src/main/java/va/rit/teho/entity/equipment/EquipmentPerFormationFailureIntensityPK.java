package va.rit.teho.entity.equipment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EquipmentPerFormationFailureIntensityPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    @JoinColumn(name = "formation_id")
    private EquipmentPerFormation equipmentPerFormation;

    @Column(name = "stage_id")
    private Long stageId;

    @Column(name = "repair_type_id")
    private Long repairTypeId;

    @Column(name = "session_id")
    private UUID sessionId;

    public EquipmentPerFormationFailureIntensityPK(Long formationId,
                                                   Long equipmentId,
                                                   Long stageId,
                                                   Long repairTypeId,
                                                   UUID sessionId) {
        this.equipmentPerFormation = new EquipmentPerFormation(formationId, equipmentId);
        this.stageId = stageId;
        this.repairTypeId = repairTypeId;
        this.sessionId = sessionId;
    }

    public EquipmentPerFormationFailureIntensityPK() {
    }

    public EquipmentPerFormation getEquipmentPerFormation() {
        return equipmentPerFormation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPerFormationFailureIntensityPK that = (EquipmentPerFormationFailureIntensityPK) o;
        return Objects.equals(equipmentPerFormation, that.equipmentPerFormation) &&
                Objects.equals(stageId, that.stageId) &&
                Objects.equals(repairTypeId, that.repairTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerFormation, stageId, repairTypeId, sessionId);
    }

    public Long getFormationId() {
        return equipmentPerFormation.getId().getFormationId();
    }

    public Long getEquipmentId() {
        return equipmentPerFormation.getId().getEquipmentId();
    }

    public Long getStageId() {
        return stageId;
    }

    public Long getRepairTypeId() {
        return repairTypeId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public EquipmentPerFormationFailureIntensityPK copy(UUID newSessionId, Long newFormationId) {
        return new EquipmentPerFormationFailureIntensityPK(newFormationId,
                                                           getEquipmentId(),
                                                           stageId,
                                                           repairTypeId,
                                                           newSessionId);
    }
}

