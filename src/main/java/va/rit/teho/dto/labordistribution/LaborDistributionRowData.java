package va.rit.teho.dto.labordistribution;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.table.RowData;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LaborDistributionRowData<T> extends RowData<Map<String, T>> {
    private final String equipmentName;
    private final Integer equipmentAmount;
    private final Double avgDailyFailure;
    private final Integer standardLaborInput;
    private final Double totalLaborInput;

    public LaborDistributionRowData(String name,
                                    Map<String, T> data,
                                    String equipmentName,
                                    Integer equipmentAmount,
                                    Double avgDailyFailure,
                                    Integer standardLaborInput,
                                    Double totalLaborInput) {
        super(null, name, data);
        this.equipmentName = equipmentName;
        this.equipmentAmount = equipmentAmount;
        this.avgDailyFailure = avgDailyFailure;
        this.standardLaborInput = standardLaborInput;
        this.totalLaborInput = totalLaborInput;
    }

    public Integer getEquipmentAmount() {
        return equipmentAmount;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public Integer getStandardLaborInput() {
        return standardLaborInput;
    }

    public Double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public Double getTotalLaborInput() {
        return totalLaborInput;
    }
}
