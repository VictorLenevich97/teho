package va.rit.teho.entity.intensity;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class IntensityData {

    private final Map<Equipment, Map<Stage, Map<RepairType, Double>>> data;

    public IntensityData(Map<Equipment, Map<Stage, Map<RepairType, Double>>> data) {
        this.data = data;
    }

    public Set<Equipment> getEquipmentSet() {
        return data.keySet();
    }

    public Double get(Equipment e, RepairType rt, Stage s) {
        return data.getOrDefault(e, Collections.emptyMap()).getOrDefault(s, Collections.emptyMap()).getOrDefault(rt, 0.0);
    }

    public Map<Equipment, Map<Stage, Map<RepairType, Double>>> getData() {
        return data;
    }
}
