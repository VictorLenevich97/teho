package va.rit.teho.dto.labordistribution;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.table.RowData;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LaborDistributionRowData<T> extends RowData<Map<String, T>> {
    private final String equipmentName;
    private final String avgDailyFailure;
    private final Integer standardLaborInput;
    private final String totalLaborInput;

    public LaborDistributionRowData(String name,
                                    Map<String, T> data,
                                    String equipmentName,
                                    String avgDailyFailure,
                                    Integer standardLaborInput,
                                    String totalLaborInput) {
        super(null, name, data);
        this.equipmentName = equipmentName;
        this.avgDailyFailure = avgDailyFailure;
        this.standardLaborInput = standardLaborInput;
        this.totalLaborInput = totalLaborInput;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public Integer getStandardLaborInput() {
        return standardLaborInput;
    }

    public String getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public String getTotalLaborInput() {
        return totalLaborInput;
    }
}
