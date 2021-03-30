package va.rit.teho.entity.intensity.combined;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.intensity.IntensityData;

import java.util.List;

public class EquipmentIntensityCombinedData {

    private final List<Stage> stages;
    private final List<RepairType> repairTypes;
    private final IntensityData intensitiesForOperation;
    private final List<Equipment> equipmentList;

    public EquipmentIntensityCombinedData(List<Stage> stages, List<RepairType> repairTypes, IntensityData intensitiesForOperation, List<Equipment> equipmentList) {
        this.stages = stages;
        this.repairTypes = repairTypes;
        this.intensitiesForOperation = intensitiesForOperation;
        this.equipmentList = equipmentList;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public List<RepairType> getRepairTypes() {
        return repairTypes;
    }

    public IntensityData getIntensitiesForOperation() {
        return intensitiesForOperation;
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }
}
