package va.rit.teho.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "base")
public class Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String shortName;
    private String fullName;
    @OneToMany(mappedBy = "base")
    private Set<EquipmentPerBase> equipmentPerBases;

    public Base() {
    }

    public Base(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
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

    public Set<EquipmentPerBase> getEquipmentPerBases() {
        return equipmentPerBases;
    }
}
