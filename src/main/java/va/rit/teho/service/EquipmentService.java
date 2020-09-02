package va.rit.teho.service;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.model.Pair;

import java.util.List;
import java.util.Map;

public interface EquipmentService {

    List<Equipment> list();

    Equipment getEquipment(Long equipmentId);

    Long add(String name, Long subTypeId);

    List<EquipmentType> listTypes();

    Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> listGroupedByTypes();

    Map<EquipmentType, List<EquipmentSubType>> listTypesWithSubTypes();

    Pair<EquipmentType, List<EquipmentSubType>> getTypeWithSubTypes(Long typeId);

    Long addType(String shortName, String longName);

    Long addSubType(Long typeId, String shortName, String fullName);

}
