package va.rit.teho.entity.intensity;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;

import java.util.Collections;
import java.util.Map;

public class ActiveIntensityData {

    private final Map<Equipment, Map<RepairType, Map<Stage, Double>>> data;

    public ActiveIntensityData(Map<Equipment, Map<RepairType, Map<Stage, Double>>> data) {
        this.data = data;
    }

    public Double get(Equipment e, RepairType rt, Stage s) {
        return data.getOrDefault(e, Collections.emptyMap()).getOrDefault(rt, Collections.emptyMap()).getOrDefault(s, 0.0);
    }

}
