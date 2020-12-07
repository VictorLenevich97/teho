package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.formation.Formation;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "repair_formation")
public class RepairFormation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = false)
    private RepairFormationType repairFormationType;

    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @OneToMany(mappedBy = "repairFormation")
    private Set<RepairFormationUnit> repairFormationUnits;

    public Set<RepairFormationUnit> getRepairFormationUnitSet() {
        return repairFormationUnits;
    }

    public RepairFormation() {
    }

    public RepairFormation(String name, Formation formation, RepairFormationType repairFormationType) {
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
