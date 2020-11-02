package va.rit.teho.entity.common;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "repair_type")
public class RepairType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String shortName;

    private boolean calculatable;

    public RepairType(String fullName, String shortName, boolean calculatable) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.calculatable = calculatable;
    }

    public RepairType() {
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
