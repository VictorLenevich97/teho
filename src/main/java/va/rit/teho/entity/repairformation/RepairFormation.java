package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.formation.Formation;

import javax.persistence.*;

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
