package va.rit.teho.entity.session;

import org.hibernate.annotations.GenericGenerator;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.entity.labordistribution.LaborDistribution;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "teho_session")
public class TehoSession implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tehoSession", orphanRemoval = true)
    private Set<LaborDistribution> laborDistributionSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tehoSession", orphanRemoval = true)
    private Set<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tehoSession", orphanRemoval = true)
    private Set<RepairFormationUnitRepairCapability> repairFormationRepairCapabilities;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tehoSession", orphanRemoval = true)
    private Set<EquipmentPerFormationFailureIntensity> equipmentPerFormationFailureIntensities;

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

    public Set<LaborDistribution> getEquipmentInRepairSet() {
        return laborDistributionSet;
    }

    public Set<RepairFormationUnitEquipmentStaff> getRepairFormationUnitEquipmentStaffSet() {
        return repairFormationUnitEquipmentStaffSet;
    }
}
