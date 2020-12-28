package va.rit.teho.service.equipment;

import org.springframework.data.util.Pair;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensityAndLaborInput;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EquipmentPerFormationService {

    List<EquipmentPerFormation> list(Long formationId);

    void addEquipmentToFormation(Long formationId, Long equipmentId, Long amount);

    void addEquipmentToFormation(Long formationId, List<Long> equipmentId, int amount);

    void updateEquipmentInFormation(Long formationId, Long equipmentId, int amount);

    void setEquipmentPerFormationFailureIntensity(UUID sessionId,
                                                  Long formationId,
                                                  Long equipmentId,
                                                  Long repairTypeId,
                                                  Long stageId,
                                                  Integer intensity);

    List<EquipmentPerFormation> getEquipmentInFormation(Long formationId, List<Long> equipmentIds);

    List<EquipmentPerFormation> getTotalEquipmentInFormations(List<Long> equipmentIds);

    void updateAvgDailyFailureData(UUID sessionId, double coefficient);

    Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> getTotalFailureIntensityData(UUID sessionId);

    Map<Pair<Formation, Equipment>, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> getFailureIntensityData(
            UUID sessionId,
            Long formationId,
            List<Long> equipmentIds);

    List<EquipmentPerFormationFailureIntensityAndLaborInput> listWithIntensityAndLaborInput(UUID sessionId,
                                                                                            Long repairTypeId);

    void copyEquipmentPerFormationData(UUID originalSessionId, UUID newSessionId);

}
