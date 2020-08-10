package by.varb.teho.service;

public interface BaseService {

    void add(String shortName, String fullName);

    void addEquipmentToBase(Long baseId, Long equipmentId, int intensity, int amount);
}
