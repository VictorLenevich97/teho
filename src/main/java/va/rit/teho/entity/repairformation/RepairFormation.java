package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.formation.Formation;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "repair_formation")
public class RepairFormation {

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = false)
    private RepairFormationType repairFormationType;

    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @OneToMany(mappedBy = "repairFormation", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<RepairFormationUnit> repairFormationUnits;

    public RepairFormation() {
    }

    public RepairFormation(Long id, String name, Formation formation, RepairFormationType repairFormationType) {
        this.id = id;
        this.name = name;
        this.formation = formation;
        this.repairFormationType = repairFormationType;
    }

    public Set<RepairFormationUnit> getRepairFormationUnitSet() {
        return repairFormationUnits;
    }

    public RepairFormationType getRepairFormationType() {
        return repairFormationType;
    }

    public void setRepairFormationType(RepairFormationType repairFormationType) {
        this.repairFormationType = repairFormationType;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
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
}
