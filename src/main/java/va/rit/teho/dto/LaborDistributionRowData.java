package va.rit.teho.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LaborDistributionRowData extends TableDataDTO.RowData<LaborInputDistributionDTO.CountAndLaborInputDTO> {
    private final String equipmentName;
    private final Double avgDailyFailure;
    private final Integer standardLaborInput;
    private final Double totalLaborInput;

    public String getEquipmentName() {
        return equipmentName;
    }

    public Double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public Integer getStandardLaborInput() {
        return standardLaborInput;
    }

    public Double getTotalLaborInput() {
        return totalLaborInput;
    }

    public LaborDistributionRowData(String name,
                                    Map<String, LaborInputDistributionDTO.CountAndLaborInputDTO> data,
                                    String equipmentName,
                                    Double avgDailyFailure,
                                    Integer standardLaborInput,
                                    Double totalLaborInput) {
        super(null, name, data);
        this.equipmentName = equipmentName;
        this.avgDailyFailure = avgDailyFailure;
        this.standardLaborInput = standardLaborInput;
        this.totalLaborInput = totalLaborInput;
    }
}
