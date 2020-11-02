package va.rit.teho.dto.repairstation;

import va.rit.teho.dto.table.RowData;

import java.util.Map;

public class RepairStationStaffRowData extends RowData<Map<String, Map<String, Integer>>> {
    private final String repairStationType;
    private final int amount;

    public RepairStationStaffRowData(Long id,
                                     String name,
                                     Map<String, Map<String, Integer>> data, String repairStationType, int amount) {
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
