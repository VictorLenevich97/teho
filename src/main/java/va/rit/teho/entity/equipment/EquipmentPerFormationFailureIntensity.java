package va.rit.teho.entity.equipment;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class EquipmentPerFormationFailureIntensity implements Serializable {

    @EmbeddedId
    private EquipmentPerFormationFailureIntensityPK id;

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
    private Stage stage;

    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    private RepairType repairType;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    private TehoSession tehoSession;

    int intensityPercentage;

    Double avgDailyFailure;

    public EquipmentPerFormationFailureIntensity(EquipmentPerFormationFailureIntensityPK id,
                                                 int intensity,
                                                 Double avgDailyFailure) {
        this.id = id;
        this.intensityPercentage = intensity;
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

    public EquipmentPerFormationFailureIntensity copy() {
        return new EquipmentPerFormationFailureIntensity(getEquipmentPerFormationWithRepairTypeId(),
                                                         intensityPercentage,
                                                         avgDailyFailure);
    }

    public EquipmentPerFormationFailureIntensity copy(UUID newSessionId) {
        return new EquipmentPerFormationFailureIntensity(getEquipmentPerFormationWithRepairTypeId().copy(newSessionId),
                                                         intensityPercentage,
                                                         avgDailyFailure);
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
                ", intensityPercentage=" + intensityPercentage +
                ", avgDailyFailure=" + avgDailyFailure +
                '}';
    }

    public EquipmentPerFormationFailureIntensity setAvgDailyFailure(Double avgDailyFailure) {
        this.avgDailyFailure = avgDailyFailure;
        return this;
    }
}
