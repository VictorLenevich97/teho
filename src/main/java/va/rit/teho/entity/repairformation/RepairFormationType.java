package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.labordistribution.RestorationType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class RepairFormationType {

    @Id
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "restoration_type_id", nullable = false)
    private RestorationType restorationType;

    private int workingHoursMin;

    private int workingHoursMax;

    public RepairFormationType() {
    }

    public RepairFormationType(Long id,
                               String name,
                               RestorationType restorationType,
                               int workingHoursMin,
                               int workingHoursMax) {
        this.id = id;
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

    public void setId(Long id) {
        this.id = id;
    }
}
