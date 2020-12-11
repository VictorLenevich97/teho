package va.rit.teho.entity.formation;

import va.rit.teho.entity.equipment.EquipmentPerFormation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "formation")
public class Formation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortName;

    private String fullName;

    @ManyToOne
    @JoinColumn(name = "parent_formation_id")
    private Formation parentFormation;

    @OneToMany(mappedBy = "equipment")
    private Set<EquipmentPerFormation> equipmentPerFormations;

    public Set<EquipmentPerFormation> getEquipmentPerFormations() {
        return equipmentPerFormations;
    }

    public Formation() {
    }

    public Formation(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public Formation getParentFormation() {
        return parentFormation;
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
        Formation formation = (Formation) o;
        return Objects.equals(id, formation.id) &&
                Objects.equals(shortName, formation.shortName) &&
                Objects.equals(fullName, formation.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortName, fullName);
    }
}
