package va.rit.teho.entity.repairformation;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class RepairFormationUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "repair_station_type_id", referencedColumnName = "id")
    private RepairStationType repairStationType;

    private String name;

    private int stationAmount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "repair_formation_id", referencedColumnName = "id")
    private RepairFormation repairFormation;

    public RepairFormationUnit() {
        //Пустой конструктор для автоматической инициализации
    }

    public RepairFormationUnit(String name,
                               RepairStationType repairStationType,
                               int stationAmount) {
        this.name = name;
        this.repairStationType = repairStationType;
        this.stationAmount = stationAmount;
    }

    public RepairFormationUnit(String name,
                               RepairStationType repairStationType,
                               int stationAmount,
                               RepairFormation repairFormation) {
        this.name = name;
        this.repairStationType = repairStationType;
        this.stationAmount = stationAmount;
        this.repairFormation = repairFormation;
    }

    public RepairFormation getRepairFormation() {
        return repairFormation;
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
