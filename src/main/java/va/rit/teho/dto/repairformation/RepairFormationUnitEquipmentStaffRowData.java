package va.rit.teho.dto.repairformation;

import va.rit.teho.dto.table.RowData;

import java.util.Map;

public class RepairFormationUnitEquipmentStaffRowData
        extends RowData<Map<String, RepairFormationUnitEquipmentStaffDTO>> {
    private final String repairStationType;
    private final int amount;

    public RepairFormationUnitEquipmentStaffRowData(Long id,
                                                    String name,
                                                    Map<String, RepairFormationUnitEquipmentStaffDTO> data,
                                                    String repairStationType,
                                                    int amount) {
        super(id, name, data);
        this.repairStationType = repairStationType;
        this.amount = amount;
    }

    public String getRepairStationType() {
        return repairStationType;
    }

    public int getAmount() {
        return amount;
    }
}
