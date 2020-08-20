package va.rit.teho.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "equipment_sub_type_id", referencedColumnName = "id")
    private EquipmentSubType equipmentSubType;
    @OneToMany(mappedBy = "equipment")
    private Set<EquipmentPerBase> equipmentPerBases;
    @OneToMany(mappedBy = "equipment")
    private Set<EquipmentLaborInputPerType> laborInputPerTypes;

    public Equipment() {
    }

    public Equipment(String name, EquipmentSubType equipmentSubType) {
        this.name = name;
        this.equipmentSubType = equipmentSubType;
    }

    public Set<EquipmentPerBase> getEquipmentPerBases() {
        return equipmentPerBases;
    }

    public void setEquipmentPerBases(Set<EquipmentPerBase> equipmentPerBases) {
        this.equipmentPerBases = equipmentPerBases;
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
}
