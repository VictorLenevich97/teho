package va.rit.teho.entity.repairdivision;

import javax.persistence.*;

@Entity
@Table(name = "repair_division")
public class RepairDivision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_division_id")
    private RepairDivision parentRepairDivision;

    public RepairDivision() {
    }

    public RepairDivision(String name, RepairDivision parentRepairDivision) {
        this.name = name;
        this.parentRepairDivision = parentRepairDivision;
    }

    public RepairDivision getParentRepairDivision() {
        return parentRepairDivision;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
