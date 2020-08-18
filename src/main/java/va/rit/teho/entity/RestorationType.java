package va.rit.teho.entity;

import javax.persistence.*;
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
}
