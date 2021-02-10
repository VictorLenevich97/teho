package va.rit.teho.service.equipment;

import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;

public interface EquipmentTypeService {

    EquipmentType get(Long id);

    List<EquipmentType> listTypes(List<Long> typeIds);

    List<EquipmentType> listHighestLevelTypes(List<Long> typeIds);

    EquipmentType addType(String shortName, String fullName);

    EquipmentType addType(Long parentTypeId, String shortName, String fullName);

    EquipmentType updateType(Long id, String shortName, String fullName);

    EquipmentType updateType(Long id, Long parentTypeId, String shortName, String fullName);

    EquipmentType deleteType(Long id);

}
