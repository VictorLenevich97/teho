package va.rit.teho.entity.equipment;

import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "equipment_sub_type")
public class EquipmentSubType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortName;

    private String fullName;

    @ManyToOne
    @JoinColumn(name = "equipment_type_id", referencedColumnName = "id")
    private EquipmentType equipmentType;

    @OneToMany(mappedBy = "equipmentSubType", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Equipment> equipmentSet;

    @OneToMany(mappedBy = "equipmentSubType", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffSet;

    public EquipmentSubType() {
    }

    public EquipmentSubType(String shortName, String fullName, EquipmentType equipmentType) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.equipmentType = equipmentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Set<Equipment> getEquipmentSet() {
        return equipmentSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EquipmentSubType that = (EquipmentSubType) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(shortName, that.shortName) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(equipmentType, that.equipmentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortName, fullName, equipmentType);
    }
}
