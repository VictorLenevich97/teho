package va.rit.teho.dto.repairformation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentTypeStaffData {
    private final Long typeId;
    private final String name;
    private final List<EquipmentStaffPerSubType> subTypes;

    public EquipmentTypeStaffData(Long typeId,
                                  List<EquipmentStaffPerSubType> subTypes) {
        this.typeId = typeId;
        this.name = null;
        this.subTypes = subTypes;
    }

    public EquipmentTypeStaffData(Long typeId,
                                  String name,
                                  List<EquipmentStaffPerSubType> subTypes) {
        this.typeId = typeId;
        this.name = name;
        this.subTypes = subTypes;
    }

    public Long getTypeId() {
        return typeId;
    }

    public String getName() {
        return name;
    }

    public List<EquipmentStaffPerSubType> getSubTypes() {
        return subTypes;
    }
}
