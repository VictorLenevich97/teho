package va.rit.teho.entity.labordistribution.combined;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class CountAndLaborInputCombinedData {

    public static final CountAndLaborInputCombinedData EMPTY = new CountAndLaborInputCombinedData(Collections.emptyMap());

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountAndLaborInputCombinedData that = (CountAndLaborInputCombinedData) o;
        return Objects.equals(countAndLaborInputMap, that.countAndLaborInputMap) && Objects.equals(totalFailureAmount, that.totalFailureAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countAndLaborInputMap, totalFailureAmount);
    }

    public Map<Long, CountAndLaborInput> getCountAndLaborInputMap() {
        return countAndLaborInputMap;
    }

    public Double getTotalFailureAmount() {
        return totalFailureAmount;
    }
}
