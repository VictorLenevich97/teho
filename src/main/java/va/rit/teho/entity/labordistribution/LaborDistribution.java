package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class LaborDistribution {

    @EmbeddedId
    private LaborDistributionPK laborDistributionId;

    @ManyToOne
    @MapsId("formation_id")
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @MapsId("workhours_distribution_interval_id")
    @JoinColumn(name = "workhours_distribution_interval_id")
    private WorkhoursDistributionInterval workhoursDistributionInterval;

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

    private double count;
    private double avgLaborInput;

    public LaborDistribution() {
        //Пустой конструктор для инициализации
    }

    public LaborDistribution(LaborDistributionPK laborDistributionId,
                             double count,
                             double avgLaborInput) {
        this.laborDistributionId = laborDistributionId;
        this.count = count;
        this.avgLaborInput = avgLaborInput;
    }

    public LaborDistribution(LaborDistributionPK laborDistributionId,
                             Formation formation,
                             Equipment equipment,
                             WorkhoursDistributionInterval workhoursDistributionInterval,
                             RepairType repairType,
                             double count,
                             double avgLaborInput) {
        this.laborDistributionId = laborDistributionId;
        this.formation = formation;
        this.equipment = equipment;
        this.workhoursDistributionInterval = workhoursDistributionInterval;
        this.repairType = repairType;
        this.count = count;
        this.avgLaborInput = avgLaborInput;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public double getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAvgLaborInput() {
        return avgLaborInput;
    }

    public void setAvgLaborInput(int avgLaborInput) {
        this.avgLaborInput = avgLaborInput;
    }

    public LaborDistributionPK getEquipmentInRepairId() {
        return laborDistributionId;
    }

    public void setEquipmentInRepairId(LaborDistributionPK laborDistributionId) {
        this.laborDistributionId = laborDistributionId;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public WorkhoursDistributionInterval getWorkhoursDistributionInterval() {
        return workhoursDistributionInterval;
    }

    public void setWorkhoursDistributionInterval(WorkhoursDistributionInterval workhoursDistributionInterval) {
        this.workhoursDistributionInterval = workhoursDistributionInterval;
    }

    public TehoSession getTehoSession() {
        return tehoSession;
    }

    public void setTehoSession(TehoSession tehoSession) {
        this.tehoSession = tehoSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaborDistribution that = (LaborDistribution) o;
        return Double.compare(that.count, count) == 0 &&
                Double.compare(that.avgLaborInput, avgLaborInput) == 0 &&
                Objects.equals(laborDistributionId, that.laborDistributionId) &&
                Objects.equals(formation, that.formation) &&
                Objects.equals(equipment, that.equipment) &&
                Objects.equals(workhoursDistributionInterval, that.workhoursDistributionInterval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(laborDistributionId,
                            formation, equipment, workhoursDistributionInterval, count, avgLaborInput);
    }

    public LaborDistribution copy(UUID newSessionId) {
        return new LaborDistribution(getEquipmentInRepairId().copy(newSessionId), getCount(), getAvgLaborInput());
    }
}
