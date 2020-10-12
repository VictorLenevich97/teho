package va.rit.teho.dto;

import java.util.Arrays;

public class LaborDistributionNestedColumnsDTO extends NestedColumnsDTO {
    private final Integer lowerBound;
    private final Integer upperBound;

    public LaborDistributionNestedColumnsDTO(Long key,
                                             Integer lowerBound,
                                             Integer upperBound) {
        super(Arrays.asList(new NestedColumnsDTO(Arrays.asList(key.toString(), "count"), "Количество"),
                            new NestedColumnsDTO(Arrays.asList(key.toString(), "laborInput"), "Qij")));
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }
}
