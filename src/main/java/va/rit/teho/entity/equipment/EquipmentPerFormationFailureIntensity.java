package va.rit.teho.entity.equipment;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
public class EquipmentPerFormationFailureIntensity implements Serializable {

    @EmbeddedId
    private EquipmentPerFormationFailureIntensityPK id;

    @ManyToOne
    @MapsId("formation_id")
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @MapsId("stage_id")
    @JoinColumn(name = "stage_id")
    private Stage stage;

    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    private RepairType repairType;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    private TehoSession tehoSession;

    private Double avgDailyFailure;

    public EquipmentPerFormationFailureIntensity(EquipmentPerFormationFailureIntensityPK id,
                                                 Double avgDailyFailure) {
        this.id = id;
        this.avgDailyFailure = avgDailyFailure;
    }

    public EquipmentPerFormationFailureIntensity(UUID sessionId,
                                                 Long formationId,
                                                 Long equipmentId,
                                                 Long stageId,
                                                 Long repairTypeId,
                                                 Double avgDailyFailure) {
        this.id = new EquipmentPerFormationFailureIntensityPK(formationId,
                                                              equipmentId,
                                                              stageId,
                                                              repairTypeId,
                                                              sessionId);
        this.avgDailyFailure = avgDailyFailure;
    }

    public EquipmentPerFormationFailureIntensity() {
    }

    public EquipmentPerFormationFailureIntensityPK getEquipmentPerFormationWithRepairTypeId() {
        return id;
    }

    public Formation getFormation() {
        return formation;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Stage getStage() {
        return stage;
    }

    public TehoSession getTehoSession() {
        return tehoSession;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public Double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public EquipmentPerFormationFailureIntensity setAvgDailyFailure(Double avgDailyFailure) {
        this.avgDailyFailure = avgDailyFailure;
        return this;
    }

    public EquipmentPerFormationFailureIntensity copy(UUID newSessionId, Formation newFormation) {
        return new EquipmentPerFormationFailureIntensity(getEquipmentPerFormationWithRepairTypeId().copy(newSessionId, newFormation.getId()),
                                                         avgDailyFailure);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPerFormationFailureIntensity that = (EquipmentPerFormationFailureIntensity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(formation, that.formation) &&
                Objects.equals(equipment, that.equipment) &&
                Objects.equals(stage, that.stage) &&
                Objects.equals(repairType, that.repairType) &&
                Objects.equals(tehoSession, that.tehoSession) &&
                Objects.equals(avgDailyFailure, that.avgDailyFailure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, formation, equipment, stage, repairType, tehoSession, avgDailyFailure);
    }

    @Override
    public String toString() {
        return "EquipmentPerBaseFailureIntensity{" +
                "id=" + id +
                ", base=" + formation +
                ", equipment=" + equipment +
                ", stage=" + stage +
                ", repairType=" + repairType +
                ", tehoSession=" + tehoSession +
                ", avgDailyFailure=" + avgDailyFailure +
                '}';
    }
}
