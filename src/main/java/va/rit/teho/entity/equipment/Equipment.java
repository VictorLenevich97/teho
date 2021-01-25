package va.rit.teho.entity.equipment;

import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "equipment")
public class Equipment implements Serializable {

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "equipment_sub_type_id", referencedColumnName = "id")
    private EquipmentSubType equipmentSubType;

    @OneToMany(mappedBy = "equipment", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<EquipmentLaborInputPerType> laborInputPerTypes;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<EquipmentPerFormation> equipmentPerFormations;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<RepairFormationUnitRepairCapability> repairCapabilities;

    public Equipment() {
    }

    public Equipment(Long id, String name, EquipmentSubType equipmentSubType) {
        this.id = id;
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
