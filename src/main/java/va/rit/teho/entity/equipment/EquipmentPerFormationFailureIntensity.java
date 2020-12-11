package va.rit.teho.entity.equipment;

import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class EquipmentPerFormationFailureIntensity implements Serializable {

    @EmbeddedId
    EquipmentPerFormationFailureIntensityPK equipmentPerFormationWithStageAndRepairType;

    @ManyToOne
    @MapsId("formation_id")
    @JoinColumn(name = "formation_id")
    Formation formation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    @ManyToOne
    @MapsId("stage_id")
    @JoinColumn(name = "stage_id")
    Stage stage;

    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    RepairType repairType;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    TehoSession tehoSession;

    int intensityPercentage;

    Double avgDailyFailure;

    public EquipmentPerFormationFailureIntensity(EquipmentPerFormationFailureIntensityPK equipmentPerFormationWithStageAndRepairType,
                                                 int intensity,
                                                 Double avgDailyFailure) {
        this.equipmentPerFormationWithStageAndRepairType = equipmentPerFormationWithStageAndRepairType;
        this.intensityPercentage = intensity;
        this.avgDailyFailure = avgDailyFailure;
    }

    public EquipmentPerFormationFailureIntensity() {
    }

    public EquipmentPerFormationFailureIntensityPK getEquipmentPerFormationWithRepairTypeId() {
        return equipmentPerFormationWithStageAndRepairType;
    }

    public Formation getFormation() {
        return formation;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public int getIntensityPercentage() {
        return intensityPercentage;
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

    public EquipmentPerFormationFailureIntensity copy(UUID newSessionId) {
        return new EquipmentPerFormationFailureIntensity(getEquipmentPerFormationWithRepairTypeId().copy(newSessionId),
                                                         intensityPercentage,
                                                         avgDailyFailure);
    }

    @Override
    public String toString() {
        return "EquipmentPerBaseFailureIntensity{" +
                "equipmentPerBaseWithStageAndRepairType=" + equipmentPerFormationWithStageAndRepairType +
                ", base=" + formation +
                ", equipment=" + equipment +
                ", stage=" + stage +
                ", repairType=" + repairType +
                ", tehoSession=" + tehoSession +
                ", intensityPercentage=" + intensityPercentage +
                ", avgDailyFailure=" + avgDailyFailure +
                '}';
    }

    public void setAvgDailyFailure(Double avgDailyFailure) {
        this.avgDailyFailure = avgDailyFailure;
    }
}
