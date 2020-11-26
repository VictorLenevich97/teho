package va.rit.teho.entity.base;

import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.equipment.EquipmentPerBase;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "base")
public class Base implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortName;

    private String fullName;

    @OneToMany(mappedBy = "equipment")
    private Set<EquipmentPerBase> equipmentPerBases;

    public Set<EquipmentPerBase> getEquipmentPerBases() {
        return equipmentPerBases;
    }

    public Base() {
    }

    public Base(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Base base = (Base) o;
        return Objects.equals(id, base.id) &&
                Objects.equals(shortName, base.shortName) &&
                Objects.equals(fullName, base.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortName, fullName);
    }
}
