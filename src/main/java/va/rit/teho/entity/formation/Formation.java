package va.rit.teho.entity.formation;

import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.repairformation.RepairFormation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "formation")
public class Formation implements Serializable {

    @Id
    private Long id;

    private String shortName;

    private String fullName;

    @ManyToOne
    @JoinColumn(name = "parent_formation_id")
    private Formation parentFormation;

    @OneToMany(mappedBy = "equipment")
    private Set<EquipmentPerFormation> equipmentPerFormations;

    @OneToMany(mappedBy = "formation")
    private Set<RepairFormation> repairFormations;

    public Set<RepairFormation> getRepairFormations() {
        return repairFormations;
    }

    public Set<Formation> getChildFormations() {
        return childFormations;
    }

    @OneToMany(mappedBy = "parentFormation")
    private Set<Formation> childFormations;

    public Set<EquipmentPerFormation> getEquipmentPerFormations() {
        return equipmentPerFormations;
    }

    public Formation() {
    }

    public Formation(Long id, String shortName, String fullName) {
        this.id = id;
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public Formation(Long id, String shortName, String fullName, Formation parentFormation) {
        this.id = id;
        this.shortName = shortName;
        this.fullName = fullName;
        this.parentFormation = parentFormation;
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

    public void setParentFormation(Formation parentFormation) {
        this.parentFormation = parentFormation;
    }
}
