package va.rit.teho.entity;

import javax.persistence.*;

@Entity
public class RepairStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "repair_station_id", referencedColumnName = "id")
    private RepairStationType repairStationType;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "base_id", referencedColumnName = "id")
    private Base base;
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
}
