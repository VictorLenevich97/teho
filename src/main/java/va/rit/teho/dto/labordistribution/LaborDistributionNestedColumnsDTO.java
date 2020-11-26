package va.rit.teho.dto.labordistribution;

import va.rit.teho.dto.table.NestedColumnsDTO;

import java.util.Arrays;

public class LaborDistributionNestedColumnsDTO extends NestedColumnsDTO {
    private final Integer from;
    private final Integer to;

    public LaborDistributionNestedColumnsDTO(Long key,
                                             Integer lowerBound,
                                             Integer upperBound) {
        super(Arrays.asList(new NestedColumnsDTO(Arrays.asList(key.toString(), "count"), "Количество"),
                            new NestedColumnsDTO(Arrays.asList(key.toString(), "laborInput"), "Qij, чел.-час.")));
        this.from = lowerBound;
        this.to = upperBound;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }
}
