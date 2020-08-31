package va.rit.teho.entity;

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

    @OneToMany(mappedBy = "restorationType")
    private Set<WorkhoursDistributionInterval> workhoursDistributionIntervals;

    public RestorationType(String name) {
        this.name = name;
    }

    public RestorationType() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestorationType that = (RestorationType) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(workhoursDistributionIntervals, that.workhoursDistributionIntervals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, workhoursDistributionIntervals);
    }
}
