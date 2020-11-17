package va.rit.teho.entity.repairdivision;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class RepairDivisionUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "repair_station_type_id", referencedColumnName = "id")
    private RepairStationType repairStationType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "division_unit_type_id", referencedColumnName = "id")
    private RepairDivisionUnitType repairDivisionUnitType;

    private String name;

    private int stationAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "division_id", referencedColumnName = "id")
    private RepairDivision repairDivision;

    public RepairDivisionUnit() {
        //Пустой конструктор для автоматической инициализации
    }

    public RepairDivisionUnit(String name, RepairStationType repairStationType,
                              RepairDivisionUnitType repairDivisionUnitType,
                              int stationAmount) {
        this.name = name;
        this.repairStationType = repairStationType;
        this.stationAmount = stationAmount;
    }

    public RepairDivisionUnit(String name,
                              RepairStationType repairStationType,
                              RepairDivisionUnitType repairDivisionUnitType,
                              int stationAmount,
                              RepairDivision subdivision) {
        this.name = name;
        this.repairStationType = repairStationType;
        this.repairDivisionUnitType = repairDivisionUnitType;
        this.stationAmount = stationAmount;
        this.repairDivision = subdivision;
    }

    public RepairDivisionUnitType getRepairDivisionUnitType() {
        return repairDivisionUnitType;
    }


    public RepairDivision getRepairDivision() {
        return repairDivision;
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
        RepairDivisionUnit that = (RepairDivisionUnit) o;
        return stationAmount == that.stationAmount &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(repairStationType, that.repairStationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, repairStationType, stationAmount);
    }

    public void setRepairDivisionUnitType(RepairDivisionUnitType repairDivisionUnitType) {
        this.repairDivisionUnitType = repairDivisionUnitType;
    }
}
