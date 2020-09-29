package va.rit.teho.service;

import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.model.Pair;

import java.util.List;
import java.util.Map;

public interface EquipmentTypeService {

    List<EquipmentType> listTypes(List<Long> typeIds);

    Map<EquipmentType, List<EquipmentSubType>> listTypesWithSubTypes(List<Long> typeIds, List<Long> subTypeIds);

    Pair<EquipmentType, List<EquipmentSubType>> getTypeWithSubTypes(Long typeId);

    Long addType(String shortName, String fullName);

    void updateType(Long id, String shortName, String fullName);

    Long addSubType(Long typeId, String shortName, String fullName);

    EquipmentSubType getSubType(Long subTypeId);

    void updateSubType(Long id, Long typeId, String shortName, String fullName);

}
