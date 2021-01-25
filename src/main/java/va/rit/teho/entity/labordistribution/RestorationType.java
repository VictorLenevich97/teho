package va.rit.teho.entity.labordistribution;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "restoration_type")
public class RestorationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int weight;

    @OneToMany(mappedBy = "restorationType", cascade = CascadeType.PERSIST)
    private Set<WorkhoursDistributionInterval> workhoursDistributionIntervals;

    public RestorationType(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public RestorationType() {
    }

    public RestorationType(Long id, String name, int weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
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

    public Set<WorkhoursDistributionInterval> getWorkhoursDistributionIntervals() {
        return workhoursDistributionIntervals;
    }

    public void setWorkhoursDistributionIntervals(Set<WorkhoursDistributionInterval> workhoursDistributionIntervals) {
        this.workhoursDistributionIntervals = workhoursDistributionIntervals;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestorationType that = (RestorationType) o;
        return weight == that.weight && Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, weight);
    }
}
