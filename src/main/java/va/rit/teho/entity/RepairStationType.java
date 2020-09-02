package va.rit.teho.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class RepairStationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int workingHoursMin;

    private int workingHoursMax;

    public RepairStationType(String name, int workingHoursMin, int workingHoursMax) {
        this.name = name;
        this.workingHoursMin = workingHoursMin;
        this.workingHoursMax = workingHoursMax;
    }

    public RepairStationType() {
        //Пустой конструктор для автоматической инициализации
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

    public int getWorkingHoursMin() {
        return workingHoursMin;
    }

    public void setWorkingHoursMin(int workingHoursMin) {
        this.workingHoursMin = workingHoursMin;
    }

    public int getWorkingHoursMax() {
        return workingHoursMax;
    }

    public void setWorkingHoursMax(int workingHoursMax) {
        this.workingHoursMax = workingHoursMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairStationType that = (RepairStationType) o;
        return workingHoursMin == that.workingHoursMin &&
                workingHoursMax == that.workingHoursMax &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, workingHoursMin, workingHoursMax);
    }
}
