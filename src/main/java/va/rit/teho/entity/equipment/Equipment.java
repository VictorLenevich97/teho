package va.rit.teho.entity.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "equipment")
public class Equipment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "equipment_sub_type_id", referencedColumnName = "id")
    private EquipmentSubType equipmentSubType;

    @JsonIgnore
    @OneToMany(mappedBy = "equipment")
    private Set<EquipmentLaborInputPerType> laborInputPerTypes;

    public Equipment() {
    }

    public Equipment(String name, EquipmentSubType equipmentSubType) {
        this.name = name;
        this.equipmentSubType = equipmentSubType;
    }

    public Set<EquipmentLaborInputPerType> getLaborInputPerTypes() {
        return laborInputPerTypes;
    }

    public void setLaborInputPerTypes(Set<EquipmentLaborInputPerType> laborInputPerTypes) {
        this.laborInputPerTypes = laborInputPerTypes;
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

    public EquipmentSubType getEquipmentSubType() {
        return equipmentSubType;
    }

    public void setEquipmentSubType(EquipmentSubType equipmentSubType) {
        this.equipmentSubType = equipmentSubType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(id, equipment.id) &&
                Objects.equals(name, equipment.name) &&
                Objects.equals(equipmentSubType, equipment.equipmentSubType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, equipmentSubType);
    }
}
