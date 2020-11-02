package va.rit.teho.dto.equipment;

import va.rit.teho.dto.table.RowData;

import java.util.Map;

public class EquipmentLaborInputPerTypeRowData extends RowData<Map<String, Integer>> {
    private final String subType;

    public EquipmentLaborInputPerTypeRowData(String name, String subType, Map<String, Integer> data) {
        super(name, data);
        this.subType = subType;
    }

    public String getSubType() {
        return subType;
    }
}
