package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistribution;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class RepairFormationUnit {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repair_station_type_id", referencedColumnName = "id", nullable = false)
    private RepairStationType repairStationType;

    @ManyToOne
    @JoinColumn(name = "workhours_distribution_interval_id")
    private WorkhoursDistributionInterval workhoursDistributionInterval;

    @ManyToOne
    @JoinColumn(name = "repair_type_id")
    private RepairType repairType;

    @Column(unique = true, nullable = false)
    private String name;

    private int stationAmount;

    @ManyToOne
    @JoinColumn(name = "repair_formation_id", referencedColumnName = "id", nullable = false)
    private RepairFormation repairFormation;

    @OneToMany(mappedBy = "repairFormationUnit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffSet;

    @OneToMany(mappedBy = "repairFormationUnit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<RepairFormationUnitRepairCapability> repairCapabilities;

    @OneToMany(mappedBy = "repairFormationUnit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<EquipmentRFUDistribution> equipmentRFUDistributions;

    public RepairFormationUnit() {
        //Пустой конструктор для автоматической инициализации
    }

    public RepairFormationUnit(Long id,
                               String name,
                               RepairStationType repairStationType,
                               int stationAmount,
                               RepairFormation repairFormation,
                               WorkhoursDistributionInterval interval,
                               RepairType repairType) {
        this.id = id;
        this.name = name;
        this.repairStationType = repairStationType;
        this.stationAmount = stationAmount;
        this.repairFormation = repairFormation;
        this.workhoursDistributionInterval = interval;
        this.repairType = repairType;
    }

    public void setWorkhoursDistributionInterval(WorkhoursDistributionInterval workhoursDistributionInterval) {
        this.workhoursDistributionInterval = workhoursDistributionInterval;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public Set<RepairFormationUnitRepairCapability> getRepairCapabilities() {
        return repairCapabilities;
    }

    public RepairFormation getRepairFormation() {
        return repairFormation;
    }

    public void setRepairFormation(RepairFormation repairFormation) {
        this.repairFormation = repairFormation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RepairStationType getRepairStationType() {
        return repairStationType;
    }

    public void setRepairStationType(RepairStationType repairStationType) {
        this.repairStationType = repairStationType;
    }

    public int getStationAmount() {
        return stationAmount;
    }

    public void setStationAmount(int stationAmount) {
        this.stationAmount = stationAmount;
    }

    public WorkhoursDistributionInterval getWorkhoursDistributionInterval() {
        return workhoursDistributionInterval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairFormationUnit that = (RepairFormationUnit) o;
        return stationAmount == that.stationAmount &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(repairStationType, that.repairStationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, repairStationType, stationAmount);
    }

}
