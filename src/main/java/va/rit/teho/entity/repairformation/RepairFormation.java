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

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = false)
    private RepairFormationType repairFormationType;

    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @OneToMany(mappedBy = "repairFormation", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<RepairFormationUnit> repairFormationUnits;

    public Set<RepairFormationUnit> getRepairFormationUnitSet() {
        return repairFormationUnits;
    }

    public RepairFormation() {
    }

    public RepairFormation(Long id, String name, Formation formation, RepairFormationType repairFormationType) {
        this.id = id;
        this.name = name;
        this.formation = formation;
        this.repairFormationType = repairFormationType;
    }

    public RepairFormationType getRepairFormationType() {
        return repairFormationType;
    }

    public Formation getFormation() {
        return formation;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
