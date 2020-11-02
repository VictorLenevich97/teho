package va.rit.teho.entity.repairstation;

import va.rit.teho.entity.base.Base;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class RepairStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "repair_station_type_id", referencedColumnName = "id")
    private RepairStationType repairStationType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "base_id", referencedColumnName = "id")
    private Base base;

    private String name;

    private int stationAmount;

    public RepairStation() {
        //Пустой конструктор для автоматической инициализации
    }

    public RepairStation(String name, RepairStationType repairStationType, Base base, int stationAmount) {
        this.name = name;
        this.repairStationType = repairStationType;
        this.base = base;
        this.stationAmount = stationAmount;
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

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
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
        RepairStation that = (RepairStation) o;
        return stationAmount == that.stationAmount &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(repairStationType, that.repairStationType) &&
                Objects.equals(base, that.base);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, repairStationType, base, stationAmount);
    }
}
