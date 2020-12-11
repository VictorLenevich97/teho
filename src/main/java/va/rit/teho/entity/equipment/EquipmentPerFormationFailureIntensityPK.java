package va.rit.teho.entity.equipment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EquipmentPerFormationFailureIntensityPK implements Serializable {

    @Column(name = "formation_id")
    Long formationId;
    @Column(name = "equipment_id")
    Long equipmentId;
    @Column(name = "stage_id")
    Long stageId;
    @Column(name = "repair_type_id")
    Long repairTypeId;
    @Column(name = "session_id")
    UUID sessionId;

    public EquipmentPerFormationFailureIntensityPK(Long formationId,
                                                   Long equipmentId,
                                                   Long stageId,
                                                   Long repairTypeId,
                                                   UUID sessionId) {
        this.formationId = formationId;
        this.equipmentId = equipmentId;
        this.stageId = stageId;
        this.repairTypeId = repairTypeId;
        this.sessionId = sessionId;
    }

    public EquipmentPerFormationFailureIntensityPK(EquipmentPerFormationFailureIntensityAndAmount epbfi, UUID sessionId) {
        this(epbfi.getFormationId(), epbfi.getEquipmentId(), epbfi.getStageId(), epbfi.getRepairTypeId(), sessionId);
    }

    public EquipmentPerFormationFailureIntensityPK() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPerFormationFailureIntensityPK that = (EquipmentPerFormationFailureIntensityPK) o;
        return Objects.equals(formationId, that.formationId) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(stageId, that.stageId) &&
                Objects.equals(repairTypeId, that.repairTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formationId, equipmentId, stageId, repairTypeId, sessionId);
    }

    public Long getFormationId() {
        return formationId;
    }

    public Long getEquipmentId() {
        return equipmentId;
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

    public EquipmentPerFormationFailureIntensityPK copy(UUID newSessionId) {
        return new EquipmentPerFormationFailureIntensityPK(formationId, equipmentId, stageId, repairTypeId, newSessionId);
    }
}

