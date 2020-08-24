package va.rit.teho.service;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import java.util.List;
import java.util.Map;

public interface EquipmentService {

    List<Equipment> list();

    Long add(String name, Long subTypeId);

    List<EquipmentType> listTypes();

    Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> listGroupedByTypes();

    Map<EquipmentType, List<EquipmentSubType>> listSubTypesPerTypes();

    Long addType(String shortName, String longName);

}
