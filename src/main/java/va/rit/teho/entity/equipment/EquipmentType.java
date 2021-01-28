package va.rit.teho.entity.equipment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Entity
@Table(name = "equipment_type")
public class EquipmentType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortName;

    private String fullName;

    @ManyToOne
    @JoinColumn(name = "parent_equipment_type")
    private EquipmentType parentType;

    @OneToMany(mappedBy = "parentType", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<EquipmentType> equipmentTypes;

    @OneToMany(mappedBy = "equipmentType", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Equipment> equipmentSet;

    public EquipmentType() {
    }

    public EquipmentType(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public EquipmentType(Long id, String shortName, String fullName) {
        this.id = id;
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public EquipmentType(Long id, String shortName, String fullName, EquipmentType parentType) {
        this.id = id;
        this.shortName = shortName;
        this.fullName = fullName;
        this.parentType = parentType;
    }

    public EquipmentType(String shortName, String fullName, EquipmentType parentType) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.parentType = parentType;
    }

    public EquipmentType getParentType() {
        return parentType;
    }

    public void setParentType(EquipmentType parentType) {
        this.parentType = parentType;
    }

    public Set<EquipmentType> getEquipmentTypes() {
        return equipmentTypes;
    }

    public Set<Equipment> getEquipmentSet() {
        return equipmentSet;
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

    public void setShortName(String type) {
        this.shortName = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Stream<EquipmentType> collectLowestLevelTypes() {
        return getEquipmentTypes().isEmpty() ? Stream.of(this) : getEquipmentTypes()
                .stream()
                .flatMap(EquipmentType::collectLowestLevelTypes);
    }

    public Stream<Equipment> collectRelatedEquipment() {
        return Stream.concat(getEquipmentSet().stream(),
                             getEquipmentTypes().stream().flatMap(EquipmentType::collectRelatedEquipment));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EquipmentType that = (EquipmentType) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(shortName, that.shortName) &&
                Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortName, fullName);
    }

}
