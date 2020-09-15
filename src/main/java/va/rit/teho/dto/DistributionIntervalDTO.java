package va.rit.teho.dto;

import va.rit.teho.entity.WorkhoursDistributionInterval;

public class DistributionIntervalDTO {
    private final Long id;
    private final int from;
    private final int to;

    public DistributionIntervalDTO(Long id, int from, int to) {
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

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
