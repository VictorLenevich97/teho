package va.rit.teho.entity.common;

import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.entity.labordistribution.LaborDistribution;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "stage")
public class Stage implements Serializable {

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer stageNum;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<EquipmentPerFormationFailureIntensity> failureIntensitySet;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<LaborDistribution> laborDistributionSet;

    public Stage() {
    }

    public Stage(Long id, Integer stageNum) {
        this.id = id;
        this.stageNum = stageNum;
    }

    public Long getId() {
        return id;
    }

    public Integer getStageNum() {
        return stageNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stage stage = (Stage) o;
        return Objects.equals(id, stage.id) &&
                Objects.equals(stageNum, stage.stageNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stageNum);
    }
}
