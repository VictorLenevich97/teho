package va.rit.teho.dto.equipment;

import java.util.Map;

public class EquipmentPerBaseSaveDTO {
    Map<Long, Map<Long, Integer>> data;
    int amount;

    public Map<Long, Map<Long, Integer>> getData() {
        return data;
    }

    public int getAmount() {
        return amount;
    }

    public EquipmentPerBaseSaveDTO(Map<Long, Map<Long, Integer>> data, int amount) {
        this.data = data;
        this.amount = amount;
    }
}
