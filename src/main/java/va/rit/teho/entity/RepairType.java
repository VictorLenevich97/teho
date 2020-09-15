package va.rit.teho.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "repair_type")
public class RepairType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private boolean repairable;

    public RepairType(String name, boolean repairable) {
        this.name = name;
        this.repairable = repairable;
    }

    public boolean isRepairable() {
        return repairable;
    }

    @OneToMany(mappedBy = "repairType")
    private Set<EquipmentLaborInputPerType> laborInputPerTypes;

    public RepairType() {
    }

    public void setRepairable(boolean repairable) {
        this.repairable = repairable;
    }

    public RepairType(Long id) {
        this.id = id;
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

    public Set<EquipmentLaborInputPerType> getLaborInputPerTypes() {
        return laborInputPerTypes;
    }

    public void setLaborInputPerTypes(Set<EquipmentLaborInputPerType> laborInputPerTypes) {
        this.laborInputPerTypes = laborInputPerTypes;
    }
}
