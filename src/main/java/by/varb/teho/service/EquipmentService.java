package by.varb.teho.service;

import by.varb.teho.dto.AddNewEquipmentDTO;
import by.varb.teho.exception.TehoException;
import by.varb.teho.model.Equipment;
import by.varb.teho.model.EquipmentType;

import java.util.List;

public interface EquipmentService {

    List<Equipment> getEquipmentInfo();

    void addNewEquipment(AddNewEquipmentDTO addNewEquipmentDTO) throws TehoException;

    List<EquipmentType> getEquipmentTypes();
}
