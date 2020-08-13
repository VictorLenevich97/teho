package by.varb.teho.service;

import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.EquipmentType;

import java.util.List;

public interface EquipmentService {

    List<Equipment> list();

    Long add(String name, Long typeId);

    List<EquipmentType> listTypes();

    Long addType(String shortName, String longName);

}
