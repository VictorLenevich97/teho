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
import java.util.function.Function;

public class EquipmentFailureIntensityCombinedData {

    private final List<Stage> stages;
    private final List<RepairType> repairTypes;
    private final Map<Formation, Map<EquipmentType, List<EquipmentPerFormation>>> equipmentPerFormations;
    private final Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> failureIntensityData;
    private final Function<EquipmentPerFormationFailureIntensity, Number> intensityFunction;
    private final String unitIndicator;

    public EquipmentFailureIntensityCombinedData(List<Stage> stages,
                                                 List<RepairType> repairTypes,
                                                 Map<Formation, Map<EquipmentType, List<EquipmentPerFormation>>> equipmentPerFormations,
                                                 Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> failureIntensityData,
                                                 Function<EquipmentPerFormationFailureIntensity, Number> intensityFunction,
                                                 String unitIndicator) {
        this.stages = stages;
        this.repairTypes = repairTypes;
        this.equipmentPerFormations = equipmentPerFormations;
        this.failureIntensityData = failureIntensityData;
        this.intensityFunction = intensityFunction;
        this.unitIndicator = unitIndicator;
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

    public Function<EquipmentPerFormationFailureIntensity, Number> getIntensityFunction() {
        return intensityFunction;
    }

    public String getUnitIndicator() {
        return unitIndicator;
    }
}
