package va.rit.teho.service;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentType;

import java.util.List;

public interface EquipmentService {

    List<Equipment> list();

    Long add(String name, Long subTypeId);

    List<EquipmentType> listTypes();

    Long addType(String shortName, String longName);

}
