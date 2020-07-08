package by.varb.teho.service;

import by.varb.teho.model.Equipment;
import by.varb.teho.model.EquipmentType;

import java.util.List;
import java.util.Map;

public interface EquipmentService {
    List<Equipment> getEquipmentInfo();
    void addNewVehicle(Map<String, Object> data) throws Exception;
    List<EquipmentType> getEquipmentTypes();
}
