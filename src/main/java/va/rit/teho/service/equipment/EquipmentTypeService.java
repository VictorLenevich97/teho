package va.rit.teho.service.equipment;

import org.springframework.data.util.Pair;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Map;

public interface EquipmentTypeService {

    List<EquipmentType> listTypes(List<Long> typeIds);

    List<EquipmentSubType> listSubTypes(List<Long> typeIds);

    Map<EquipmentType, List<EquipmentSubType>> listTypesWithSubTypes(List<Long> typeIds, List<Long> subTypeIds);

    Pair<EquipmentType, List<EquipmentSubType>> getTypeWithSubTypes(Long typeId);

    EquipmentType addType(String shortName, String fullName);

    EquipmentType updateType(Long id, String shortName, String fullName);

    EquipmentType deleteType(Long id);

    EquipmentSubType addSubType(Long typeId, String shortName, String fullName);

    EquipmentSubType getSubType(Long subTypeId);

    EquipmentSubType updateSubType(Long id, Long typeId, String shortName, String fullName);

    void deleteSubType(Long id);

}
