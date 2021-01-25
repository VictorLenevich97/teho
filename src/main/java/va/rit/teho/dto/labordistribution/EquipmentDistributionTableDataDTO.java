package va.rit.teho.dto.labordistribution;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.TableDataDTO;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentDistributionTableDataDTO extends TableDataDTO<Map<String, String>> {
    private final List<NestedColumnsDTO> repairTypeColumns;
    private final List<NestedColumnsDTO> restorationTypeColumns;

    public EquipmentDistributionTableDataDTO(List<NestedColumnsDTO> repairTypeColumns,
                                             List<NestedColumnsDTO> restorationTypeColumns,
                                             List<EquipmentDistributionRowData> rows) {
        super(null, rows);
        this.repairTypeColumns = repairTypeColumns;
        this.restorationTypeColumns = restorationTypeColumns;
    }

    public List<NestedColumnsDTO> getRepairTypeColumns() {
        return repairTypeColumns;
    }

    public List<NestedColumnsDTO> getRestorationTypeColumns() {
        return restorationTypeColumns;
    }
}
