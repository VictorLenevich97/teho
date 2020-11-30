package va.rit.teho.dto.equipment;

import va.rit.teho.dto.table.RowData;

import java.util.Map;

public class EquipmentFailureIntensityRowData<T> extends RowData<Map<String, Map<String, T>>> {
    private final Long id;
    private final String equipmentName;
    private final Integer amount;

    public EquipmentFailureIntensityRowData(Long id,
                                            String formationName,
                                            String equipmentName,
                                            Integer amount,
                                            Map<String, Map<String, T>> data) {
        super(null, formationName, data);
        this.equipmentName = equipmentName;
        this.amount = amount;
        this.id = id;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public Integer getAmount() {
        return amount;
    }

    public Long getId() {
        return id;
    }

}
