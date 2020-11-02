package va.rit.teho.entity.equipment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EquipmentPerBaseFailureIntensityPK implements Serializable {

    @Column(name = "base_id")
    Long baseId;
    @Column(name = "equipment_id")
    Long equipmentId;
    @Column(name = "stage_id")
    Long stageId;
    @Column(name = "repair_type_id")
    Long repairTypeId;
    @Column(name = "session_id")
    UUID sessionId;

    public EquipmentPerBaseFailureIntensityPK(Long baseId,
                                              Long equipmentId,
                                              Long stageId,
                                              Long repairTypeId,
                                              UUID sessionId) {
        this.baseId = baseId;
        this.equipmentId = equipmentId;
        this.stageId = stageId;
        this.repairTypeId = repairTypeId;
        this.sessionId = sessionId;
    }

    public EquipmentPerBaseFailureIntensityPK(EquipmentPerBaseFailureIntensityAndAmount epbfi, UUID sessionId) {
        this(epbfi.getBaseId(), epbfi.getEquipmentId(), epbfi.getStageId(), epbfi.getRepairTypeId(), sessionId);
    }

    public EquipmentPerBaseFailureIntensityPK() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPerBaseFailureIntensityPK that = (EquipmentPerBaseFailureIntensityPK) o;
        return Objects.equals(baseId, that.baseId) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(stageId, that.stageId) &&
                Objects.equals(repairTypeId, that.repairTypeId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseId, equipmentId, stageId, repairTypeId, sessionId);
    }

    public Long getBaseId() {
        return baseId;
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
}

