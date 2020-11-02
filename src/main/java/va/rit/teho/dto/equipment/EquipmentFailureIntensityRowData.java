package va.rit.teho.dto.equipment;

import va.rit.teho.dto.table.RowData;

import java.util.Map;

public class EquipmentFailureIntensityRowData<T> extends RowData<Map<String, Map<String, T>>> {
    private final String equipmentName;
    private final Integer amount;

    public EquipmentFailureIntensityRowData(String baseName,
                                            String equipmentName,
                                            Integer amount,
                                            Map<String, Map<String, T>> data) {
        super(null, baseName, data);
        this.equipmentName = equipmentName;
        this.amount = amount;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public Integer getAmount() {
        return amount;
    }
}
