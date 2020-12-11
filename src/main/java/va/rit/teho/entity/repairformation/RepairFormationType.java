package va.rit.teho.entity.repairformation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RepairFormationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int workingHoursMin;

    private int workingHoursMax;

    public RepairFormationType() {
    }

    public RepairFormationType(String name, int workingHoursMin, int workingHoursMax) {
        this.name = name;
        this.workingHoursMin = workingHoursMin;
        this.workingHoursMax = workingHoursMax;
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
