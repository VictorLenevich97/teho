package va.rit.teho.service.equipment;

import org.springframework.data.util.Pair;
import va.rit.teho.entity.base.Base;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerBase;
import va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensity;
import va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensityAndLaborInput;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EquipmentPerBaseService {

    void addEquipmentToBase(Long baseId, Long equipmentId, int amount);

    void updateEquipmentInBase(Long baseId, Long equipmentId, int amount);

    void setEquipmentPerBaseFailureIntensity(UUID sessionId,
                                             Long baseId,
                                             Long equipmentId,
                                             Long repairTypeId,
                                             Long stageId,
                                             Integer intensity);

    List<EquipmentPerBase> getEquipmentInBases();

    void updateAvgDailyFailureData(UUID sessionId, double coefficient);

    Map<Pair<Base, Equipment>, Map<RepairType, Map<Stage, EquipmentPerBaseFailureIntensity>>> getFailureIntensityData(
            UUID sessionId,
            Long baseId);

    List<EquipmentPerBaseFailureIntensityAndLaborInput> listWithIntensityAndLaborInput(UUID sessionId,
                                                                                       Long repairTypeId);

}
