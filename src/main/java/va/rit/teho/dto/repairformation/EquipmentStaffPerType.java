package va.rit.teho.dto.repairformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairFormationUnitPK;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentStaffPerType {
    private final Long typeId;
    private final String name;
    private final Integer total;
    private final Integer available;
    private final List<RepairCapabilityPerEquipment> equipment;

    public EquipmentStaffPerType() {
        this.typeId = null;
        this.name = null;
        this.total = 0;
        this.available = 0;
        this.equipment = null;
    }

    public EquipmentStaffPerType(Long typeId, String name, Integer total, Integer available) {
        this.typeId = typeId;
        this.name = name;
        this.total = total;
        this.available = available;
        this.equipment = null;
    }

    public EquipmentStaffPerType(Long typeId,
                                 String name,
                                 Integer total,
                                 Integer available,
                                 List<RepairCapabilityPerEquipment> equipment) {
        this.typeId = typeId;
        this.name = name;
        this.total = total;
        this.available = available;
        this.equipment = equipment;
    }

    public static EquipmentStaffPerType from(RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff) {
        return new EquipmentStaffPerType(repairFormationUnitEquipmentStaff
                                                 .getEquipmentPerRepairFormationUnit()
                                                 .getEquipmentTypeId(),
                                         "",
                                         repairFormationUnitEquipmentStaff.getTotalStaff(),
                                         repairFormationUnitEquipmentStaff.getAvailableStaff());
    }

    public RepairFormationUnitEquipmentStaff toEntity(UUID sessionId, Long repairFormationUnitId) {
        return new RepairFormationUnitEquipmentStaff(new RepairFormationUnitPK(repairFormationUnitId,
                                                                               getTypeId(),
                                                                               sessionId),
                                                     getTotal(),
                                                     getAvailable());
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

    public Integer getTotal() {
        return total;
    }

    public Integer getAvailable() {
        return available;
    }
}
