package va.rit.teho.dto.equipment;

import va.rit.teho.dto.table.RowData;
import va.rit.teho.entity.equipment.Equipment;

import javax.validation.constraints.Positive;
import java.util.Map;

public class EquipmentLaborInputPerTypeRowData extends RowData<Map<String, Integer>> {

    @Positive
    private final Long subTypeId;

    private final String subTypeName;

    public EquipmentLaborInputPerTypeRowData() {
        super(null, null);
        this.subTypeName = null;
        this.subTypeId = null;
    }

    public EquipmentLaborInputPerTypeRowData(Equipment e, Map<String, Integer> data) {
        super(e.getId(), e.getName(), data);
        this.subTypeId = e.getEquipmentSubType().getId();
        this.subTypeName = e.getEquipmentSubType().getFullName();
    }

    public EquipmentLaborInputPerTypeRowData(Long id,
                                             String name,
                                             Long subTypeId,
                                             String subTypeName,
                                             Map<String, Integer> data) {
        super(id, name, data);
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
