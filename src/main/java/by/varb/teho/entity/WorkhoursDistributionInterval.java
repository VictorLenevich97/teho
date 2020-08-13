package by.varb.teho.entity;

import javax.persistence.*;

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

    public RestorationType getRestorationType() {
        return restorationType;
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

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }
}
