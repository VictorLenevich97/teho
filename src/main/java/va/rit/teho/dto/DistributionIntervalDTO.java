package va.rit.teho.dto;

import va.rit.teho.entity.WorkhoursDistributionInterval;

public class DistributionIntervalDTO {
    private final Long key;
    private final int from;
    private final int to;

    public DistributionIntervalDTO(Long key, int from, int to) {
        this.key = key;
        this.from = from;
        this.to = to;
    }

    public static DistributionIntervalDTO from(WorkhoursDistributionInterval interval) {
        return new DistributionIntervalDTO(interval.getId(), interval.getLowerBound(), interval.getUpperBound());
    }

    public Long getKey() {
        return key;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
