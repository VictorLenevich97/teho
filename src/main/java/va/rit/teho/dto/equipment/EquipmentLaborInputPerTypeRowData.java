package va.rit.teho.dto.equipment;

import va.rit.teho.dto.table.RowData;

import java.util.Map;

public class EquipmentLaborInputPerTypeRowData extends RowData<Map<String, Integer>> {
    private final Long subTypeId;
    private final String subTypeName;

    public EquipmentLaborInputPerTypeRowData(String name, Long subTypeId, String subTypeName, Map<String, Integer> data) {
        super(name, data);
        this.subTypeId = subTypeId;
        this.subTypeName = subTypeName;
    }

    public Long getSubTypeId() {
        return subTypeId;
    }

    public String getSubTypeName() {
        return subTypeName;
    }
}
