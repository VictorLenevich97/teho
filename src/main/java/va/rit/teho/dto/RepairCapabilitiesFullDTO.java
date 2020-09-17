package va.rit.teho.dto;

import va.rit.teho.dto.equipment.EquipmentSubTypeWithEquipmentPerTypeDTO;

import java.util.List;

public class RepairCapabilitiesFullDTO {
    private List<String> rows;
    private List<EquipmentSubTypeWithEquipmentPerTypeDTO> columns;
    private Double[][] data;

    public List<String> getRows() {
        return rows;
    }

    public List<EquipmentSubTypeWithEquipmentPerTypeDTO> getColumns() {
        return columns;
    }

    public Double[][] getData() {
        return data;
    }

    public RepairCapabilitiesFullDTO(List<String> rows,
                                     List<EquipmentSubTypeWithEquipmentPerTypeDTO> columns, Double[][] data) {
        this.rows = rows;
        this.columns = columns;
        this.data = data;
    }
}
