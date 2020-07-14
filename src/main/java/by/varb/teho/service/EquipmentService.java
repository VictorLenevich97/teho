package by.varb.teho.service;

import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.EquipmentType;

import java.util.List;

public interface EquipmentService {
    List<Equipment> getAll();

    void add(String name, Long typeId) throws Exception;

    List<EquipmentType> getAllTypes();

    void addType(String shortName, String longName);

}
