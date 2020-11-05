package va.rit.teho.entity.equipment;

import va.rit.teho.entity.base.Base;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class EquipmentPerBaseFailureIntensity {

    @EmbeddedId
    EquipmentPerBaseFailureIntensityPK equipmentPerBaseWithStageAndRepairType;

    @ManyToOne
    @MapsId("base_id")
    @JoinColumn(name = "base_id")
    Base base;

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

    public EquipmentPerBaseFailureIntensity(EquipmentPerBaseFailureIntensityPK equipmentPerBaseWithStageAndRepairType,
                                            int intensity,
                                            Double avgDailyFailure) {
        this.equipmentPerBaseWithStageAndRepairType = equipmentPerBaseWithStageAndRepairType;
        this.intensityPercentage = intensity;
        this.avgDailyFailure = avgDailyFailure;
    }

    public EquipmentPerBaseFailureIntensity() {
    }

    public EquipmentPerBaseFailureIntensityPK getEquipmentPerBaseWithRepairTypeId() {
        return equipmentPerBaseWithStageAndRepairType;
    }

    public Base getBase() {
        return base;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public int getIntensityPercentage() {
        return intensityPercentage;
    }

    public EquipmentPerBaseFailureIntensityPK getEquipmentPerBaseWithStageAndRepairType() {
        return equipmentPerBaseWithStageAndRepairType;
    }

    public EquipmentPerBaseFailureIntensityPK getEquipmentPerBaseWithStage() {
        return equipmentPerBaseWithStageAndRepairType;
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

    public EquipmentPerBaseFailureIntensity copy(UUID newSessionId) {
        return new EquipmentPerBaseFailureIntensity(getEquipmentPerBaseWithRepairTypeId().copy(newSessionId),
                                                    intensityPercentage,
                                                    avgDailyFailure);
    }
}
