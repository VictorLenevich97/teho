package va.rit.teho.dto.repairformation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentTypeStaffData {
    private final Long typeId;
    private final String name;
    private final Integer total;
    private final Integer available;
    private final List<EquipmentTypeStaffData> subTypes;
    private final List<RepairCapabilityPerEquipment> equipment;

    public EquipmentTypeStaffData(Long typeId,
                                  String name,
                                  Integer total,
                                  Integer available,
                                  List<EquipmentTypeStaffData> subTypes,
                                  List<RepairCapabilityPerEquipment> equipment) {
        this.typeId = typeId;
        this.name = name;
        this.total = total;
        this.available = available;
        this.subTypes = subTypes;
        this.equipment = equipment;
    }

    public EquipmentTypeStaffData(Long typeId,
                                  List<EquipmentTypeStaffData> subTypes) {
        this.typeId = typeId;
        this.name = null;
        this.total = 0;
        this.available = 0;
        this.subTypes = subTypes;
        this.equipment = null;
    }

    public EquipmentTypeStaffData(Long typeId,
                                  String name,
                                  List<EquipmentTypeStaffData> subTypes) {
        this.typeId = typeId;
        this.name = name;
        this.total = 0;
        this.available = 0;
        this.subTypes = subTypes;
        this.equipment = null;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getAvailable() {
        return available;
    }

    public List<RepairCapabilityPerEquipment> getEquipment() {
        return equipment;
    }

    public Long getTypeId() {
        return typeId;
    }

    public String getName() {
        return name;
    }

    public List<EquipmentTypeStaffData> getSubTypes() {
        return subTypes;
    }
}
