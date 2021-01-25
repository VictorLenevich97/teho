package va.rit.teho.dto.labordistribution;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.table.RowData;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentDistributionRowData extends RowData<Map<String, String>> {

    private final Integer equipmentAmount;
    private final Double failureAmount;
    private final Map<String, Double> repairTypeData;
    private final Map<String, Double> restorationTypeData;

    public EquipmentDistributionRowData(Long id,
                                        String name,
                                        Integer equipmentAmount,
                                        Double failureAmount,
                                        Map<String, Double> repairTypeData,
                                        Map<String, Double> restorationTypeData) {
        super(id, name, null);
        this.equipmentAmount = equipmentAmount;
        this.failureAmount = failureAmount;
        this.repairTypeData = repairTypeData;
        this.restorationTypeData = restorationTypeData;
    }

    public Integer getEquipmentAmount() {
        return equipmentAmount;
    }

    public Double getFailureAmount() {
        return failureAmount;
    }

    public Map<String, Double> getRepairTypeData() {
        return repairTypeData;
    }

    public Map<String, Double> getRestorationTypeData() {
        return restorationTypeData;
    }
}
