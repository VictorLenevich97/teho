package va.rit.teho.entity;

import javax.persistence.*;

@Entity
@Table(name = "equipment_sub_type")
public class EquipmentSubType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String shortName;
    private String fullName;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "equipment_type_id", referencedColumnName = "id")
    private EquipmentType equipmentType;

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
}
