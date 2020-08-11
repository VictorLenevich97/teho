package by.varb.teho.service;

import by.varb.teho.entity.Base;

import java.util.List;

public interface BaseService {

    void add(String shortName, String fullName);

    void addEquipmentToBase(Long baseId, Long equipmentId, int intensity, int amount);

    List<Base> getAll();
}
