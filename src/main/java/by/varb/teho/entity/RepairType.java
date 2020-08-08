package by.varb.teho.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "repair_type")
public class RepairType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public RepairType() {
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

    public RepairType(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "repairType")
    private Set<EquipmentLaborInputPerType> laborInputPerTypes;
}
