package va.rit.teho.entity.equipment;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.formation.Formation;

import java.util.List;
import java.util.Map;

public class EquipmentFailureIntensityCombinedData {
    List<Stage> stages;
    List<RepairType> repairTypes;
    Map<Formation, Map<EquipmentSubType, List<EquipmentPerFormation>>> equipmentPerFormations;
    Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> failureIntensityData;

    public EquipmentFailureIntensityCombinedData(List<Stage> stages,
                                                 List<RepairType> repairTypes,
                                                 Map<Formation, Map<EquipmentSubType, List<EquipmentPerFormation>>> equipmentPerFormations,
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

    public Map<Formation, Map<EquipmentSubType, List<EquipmentPerFormation>>> getEquipmentPerFormations() {
        return equipmentPerFormations;
    }

    public Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> getFailureIntensityData() {
        return failureIntensityData;
    }
}
