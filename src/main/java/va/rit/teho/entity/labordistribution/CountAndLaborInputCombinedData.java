package va.rit.teho.entity.labordistribution;

import java.util.Collections;
import java.util.Map;

public class CountAndLaborInputCombinedData {
    private final Map<Long, CountAndLaborInput> countAndLaborInputMap;
    private final Double totalFailureAmount;

    public CountAndLaborInputCombinedData(Map<Long, CountAndLaborInput> countAndLaborInputMap) {
        this.countAndLaborInputMap = countAndLaborInputMap;
        this.totalFailureAmount = 0.0;
    }

    public CountAndLaborInputCombinedData(Double totalFailureAmount) {
        this.totalFailureAmount = totalFailureAmount;
        this.countAndLaborInputMap = Collections.emptyMap();
    }

    public Map<Long, CountAndLaborInput> getCountAndLaborInputMap() {
        return countAndLaborInputMap;
    }

    public Double getTotalFailureAmount() {
        return totalFailureAmount;
    }
}
