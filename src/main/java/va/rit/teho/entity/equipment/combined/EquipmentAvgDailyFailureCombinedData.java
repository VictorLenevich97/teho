package va.rit.teho.entity.equipment.combined;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.formation.Formation;

import java.util.List;
import java.util.Map;

public class EquipmentAvgDailyFailureCombinedData {

    private final List<Stage> stages;
    private final List<RepairType> repairTypes;
    private final Map<Formation, Map<EquipmentType, List<EquipmentPerFormation>>> equipmentPerFormations;
    private final Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> failureIntensityData;

    public EquipmentAvgDailyFailureCombinedData(List<Stage> stages,
                                                List<RepairType> repairTypes,
                                                Map<Formation, Map<EquipmentType, List<EquipmentPerFormation>>> equipmentPerFormations,
                                                Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> failureIntensityData) {
        this.stages = stages;
        this.repairTypes = repairTypes;
        this.equipmentPerFormations = equipmentPerFormations;
        this.failureIntensityData = failureIntensityData;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public List<RepairType> getRepairTypes() {
        return repairTypes;
    }

    public Map<Formation, Map<EquipmentType, List<EquipmentPerFormation>>> getEquipmentPerFormations() {
        return equipmentPerFormations;
    }

    public Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> getFailureIntensityData() {
        return failureIntensityData;
    }
}
