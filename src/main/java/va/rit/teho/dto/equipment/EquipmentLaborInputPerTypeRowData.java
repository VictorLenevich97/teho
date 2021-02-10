package va.rit.teho.dto.equipment;

import va.rit.teho.dto.table.RowData;
import va.rit.teho.entity.equipment.Equipment;

import javax.validation.constraints.Positive;
import java.util.Map;

public class EquipmentLaborInputPerTypeRowData extends RowData<Map<String, Integer>> {

    @Positive
    private final Long typeId;

    private final String typeName;

    public EquipmentLaborInputPerTypeRowData() {
        super(null, null);
        this.typeName = null;
        this.typeId = null;
    }

    public EquipmentLaborInputPerTypeRowData(Equipment e, Map<String, Integer> data) {
        super(e.getId(), e.getName(), data);
        this.typeId = e.getEquipmentType().getId();
        this.typeName = e.getEquipmentType().getFullName();
    }

    public EquipmentLaborInputPerTypeRowData(Long id,
                                             String name,
                                             Long typeId,
                                             String typeName,
                                             Map<String, Integer> data) {
        super(id, name, data);
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public Long getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }
}
