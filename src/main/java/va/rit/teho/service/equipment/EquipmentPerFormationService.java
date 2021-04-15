package va.rit.teho.service.equipment;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.equipment.combined.EquipmentPerFormationFailureIntensityAndLaborInput;
import va.rit.teho.entity.formation.Formation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EquipmentPerFormationService {

    EquipmentPerFormation addEquipmentToFormation(Long formationId, Long equipmentId, Long amount);

    List<EquipmentPerFormation> addEquipmentToFormation(Long formationId, List<Long> equipmentId, Long amount);

    EquipmentPerFormation updateEquipmentInFormation(Long formationId, Long equipmentId, int amount);

    void setEquipmentPerFormationDailyFailure(UUID sessionId,
                                              Long formationId,
                                              Long equipmentId,
                                              Long repairTypeId,
                                              Long stageId,
                                              Double dailyFailure);

    void calculateAndSetEquipmentPerFormationDailyFailure(UUID sessionId,
                                                          Long formationId,
                                                          double coefficient);

    List<EquipmentPerFormation> getEquipmentInFormation(Long formationId);

    List<EquipmentPerFormation> getEquipmentInFormation(Long formationId, String nameFilter);

    List<EquipmentPerFormation> getEquipmentInAllFormations();

    Map<EquipmentType, List<EquipmentPerFormation>> getGroupedEquipmentInFormation(Long formationId, String equipmentName);

    Map<Formation, Map<EquipmentType, List<EquipmentPerFormation>>> getTotalGroupedEquipmentInFormations(List<Long> equipmentIds);

    Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> getFailureIntensityData(
            UUID sessionId,
            Long formationId,
            String equipmentName);


    Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> getTotalFailureIntensityData(
            UUID sessionId);

    List<EquipmentPerFormationFailureIntensityAndLaborInput> listWithIntensityAndLaborInput(UUID sessionId,
                                                                                            Long repairTypeId,
                                                                                            List<Long> equipmentIds,
                                                                                            List<Long> formationIds);

    void copyEquipmentPerFormationData(UUID originalSessionId, UUID newSessionId);

    void deleteEquipmentFromFormation(Long formationId, Long equipmentId);

}
