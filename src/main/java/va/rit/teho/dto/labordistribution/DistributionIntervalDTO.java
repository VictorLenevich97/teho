package va.rit.teho.dto.labordistribution;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistributionIntervalDTO {
    private final Long id;
    private final Integer from;
    private final Integer to;

    public DistributionIntervalDTO(Long id, Integer from, Integer to) {
        this.id = id;
        this.from = from;
        this.to = to;
    }

    public static DistributionIntervalDTO from(WorkhoursDistributionInterval interval) {
        return new DistributionIntervalDTO(interval.getId(), interval.getLowerBound(), interval.getUpperBound());
    }

    public Long getId() {
        return id;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }
}
