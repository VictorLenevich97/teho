package va.rit.teho.dto.labordistribution;

import va.rit.teho.dto.table.NestedColumnsDTO;

import java.util.Arrays;
import java.util.Collections;

public class LaborDistributionNestedColumnsDTO extends NestedColumnsDTO {
    private final Integer from;
    private final Integer to;

    public LaborDistributionNestedColumnsDTO(Object key,
                                             Integer lowerBound,
                                             Integer upperBound,
                                             boolean includeNested) {
        super(includeNested ? Arrays.asList(new NestedColumnsDTO(Arrays.asList(key.toString(), "count"), "Количество"),
                            new NestedColumnsDTO(Arrays.asList(key.toString(), "laborInput"), "Qij, чел.-час.")) : Collections
                .emptyList());
        if(!includeNested) {
            this.setKey(key);
        }
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
