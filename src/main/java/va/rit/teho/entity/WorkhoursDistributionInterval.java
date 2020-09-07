package va.rit.teho.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class WorkhoursDistributionInterval {

    @ManyToOne
    @MapsId("restoration_type_id")
    @JoinColumn(name = "restoration_type_id")
    RestorationType restorationType;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer lowerBound;
    private Integer upperBound;

    public WorkhoursDistributionInterval(Integer lowerBound,
                                         Integer upperBound,
                                         RestorationType restorationType) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.restorationType = restorationType;
    }

    public WorkhoursDistributionInterval() {
        //Пустой конструктор для автоматической инициализации
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
}
