package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.repairformation.RepairFormationUnit;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class WorkhoursDistributionInterval {

    @Id
    private Long id;

    private Integer lowerBound;

    private Integer upperBound;

    @ManyToOne
    @JoinColumn(name = "restoration_type_id", nullable = false)
    private RestorationType restorationType;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "workhoursDistributionInterval", orphanRemoval = true)
    private Set<RepairFormationUnit> repairFormationUnits;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workhoursDistributionInterval", orphanRemoval = true)
    private Set<LaborDistribution> laborDistributions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workhoursDistributionInterval", orphanRemoval = true)
    private Set<EquipmentRFUDistribution> equipmentRFUDistributions;

    public WorkhoursDistributionInterval(Integer lowerBound,
                                         Integer upperBound,
                                         RestorationType restorationType) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.restorationType = restorationType;
    }

    public WorkhoursDistributionInterval(Long id,
                                         Integer lowerBound,
                                         Integer upperBound,
                                         RestorationType restorationType) {
        this.id = id;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.restorationType = restorationType;
    }

    public WorkhoursDistributionInterval() {
        //Пустой конструктор для автоматической инициализации
    }

    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
    }

    public void setRestorationType(RestorationType restorationType) {
        this.restorationType = restorationType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkhoursDistributionInterval that = (WorkhoursDistributionInterval) o;
        return Objects.equals(restorationType, that.restorationType) &&
                Objects.equals(id, that.id) &&
                Objects.equals(lowerBound, that.lowerBound) &&
                Objects.equals(upperBound, that.upperBound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restorationType, id, lowerBound, upperBound);
    }

    public RestorationType getRestorationType() {
        return restorationType;
    }
}
