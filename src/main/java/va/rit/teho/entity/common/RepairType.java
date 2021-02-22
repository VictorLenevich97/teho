package va.rit.teho.entity.common;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "repair_type")
public class RepairType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String shortName;

    private boolean calculatable;

    @Column(updatable = false)
    private boolean repairable;

    private boolean includesIntervals;

    public RepairType(String fullName,
                      String shortName,
                      boolean calculatable,
                      boolean repairable,
                      boolean includesIntervals) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.calculatable = calculatable;
        this.repairable = repairable;
        this.includesIntervals = includesIntervals;
    }

    public RepairType() {
    }

    public RepairType(Long id,
                      String fullName,
                      String shortName,
                      boolean calculatable,
                      boolean repairable,
                      boolean includesIntervals) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.calculatable = calculatable;
        this.repairable = repairable;
        this.includesIntervals = includesIntervals;
    }

    public RepairType(Long id) {
        this.id = id;
    }

    public boolean isCalculatable() {
        return calculatable;
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean isRepairable() {
        return repairable;
    }

    public boolean includesIntervals() {
        return includesIntervals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairType that = (RepairType) o;
        return calculatable == that.calculatable &&
                Objects.equals(id, that.id) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(shortName, that.shortName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, shortName, calculatable);
    }
}
