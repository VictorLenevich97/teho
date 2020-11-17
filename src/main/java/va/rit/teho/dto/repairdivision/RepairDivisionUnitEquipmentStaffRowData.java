package va.rit.teho.dto.repairdivision;

import va.rit.teho.dto.table.RowData;

import java.util.Map;

public class RepairDivisionUnitEquipmentStaffRowData extends RowData<Map<String, RepairDivisionUnitEquipmentStaffDTO>> {
    private final String repairStationType;
    private final int amount;

    public RepairDivisionUnitEquipmentStaffRowData(Long id,
                                                   String name,
                                                   Map<String, RepairDivisionUnitEquipmentStaffDTO> data, String repairStationType, int amount) {
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
