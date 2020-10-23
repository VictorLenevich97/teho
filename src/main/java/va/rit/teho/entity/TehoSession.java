package va.rit.teho.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "teho_session")
public class TehoSession {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tehoSession", orphanRemoval = true)
    private Set<EquipmentInRepair> equipmentInRepairSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tehoSession", orphanRemoval = true)
    private Set<RepairStationEquipmentStaff> repairStationEquipmentStaffSet;

    @Column(name = "creation_ts")
    private Instant creationTimestamp;

    public TehoSession() {
    }

    public TehoSession(String name) {
        this.name = name;
        this.creationTimestamp = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public Set<EquipmentInRepair> getEquipmentInRepairSet() {
        return equipmentInRepairSet;
    }

    public Set<RepairStationEquipmentStaff> getRepairStationEquipmentStaffSet() {
        return repairStationEquipmentStaffSet;
    }
}
