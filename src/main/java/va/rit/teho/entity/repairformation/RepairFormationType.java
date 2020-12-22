package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.labordistribution.RestorationType;

import javax.persistence.*;

@Entity
public class RepairFormationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "restoration_type_id", nullable = false)
    private RestorationType restorationType;

    private int workingHoursMin;

    private int workingHoursMax;

    public RepairFormationType() {
    }

    public RepairFormationType(String name, RestorationType restorationType, int workingHoursMin, int workingHoursMax) {
        this.name = name;
        this.restorationType = restorationType;
        this.workingHoursMin = workingHoursMin;
        this.workingHoursMax = workingHoursMax;
    }

    public RestorationType getRestorationType() {
        return restorationType;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWorkingHoursMin() {
        return workingHoursMin;
    }

    public int getWorkingHoursMax() {
        return workingHoursMax;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorkingHoursMin(int workingHoursMin) {
        this.workingHoursMin = workingHoursMin;
    }

    public void setWorkingHoursMax(int workingHoursMax) {
        this.workingHoursMax = workingHoursMax;
    }
}
