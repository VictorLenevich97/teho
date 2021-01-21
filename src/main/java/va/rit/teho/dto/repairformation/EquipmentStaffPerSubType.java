package va.rit.teho.dto.repairformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentStaffPerSubType {
    private final Long subTypeId;
    private final String name;
    private final Integer total;
    private final Integer available;
    private final List<RepairCapabilityPerEquipment> equipment;

    public EquipmentStaffPerSubType(Long subTypeId, String name, Integer total, Integer available) {
        this.subTypeId = subTypeId;
        this.name = name;
        this.total = total;
        this.available = available;
        this.equipment = null;
    }

    public EquipmentStaffPerSubType(Long subTypeId,
                                    String name,
                                    Integer total,
                                    Integer available,
                                    List<RepairCapabilityPerEquipment> equipment) {
        this.subTypeId = subTypeId;
        this.name = name;
        this.total = total;
        this.available = available;
        this.equipment = equipment;
    }

    public List<RepairCapabilityPerEquipment> getEquipment() {
        return equipment;
    }

    public Long getSubTypeId() {
        return subTypeId;
    }

    public String getName() {
        return name;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getAvailable() {
        return available;
    }

    public static EquipmentStaffPerSubType from(RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff) {
        return new EquipmentStaffPerSubType(repairFormationUnitEquipmentStaff.getEquipmentSubType().getId(),
                                            repairFormationUnitEquipmentStaff.getEquipmentSubType().getFullName(),
                                            repairFormationUnitEquipmentStaff.getTotalStaff(),
                                            repairFormationUnitEquipmentStaff.getAvailableStaff());
    }
}
