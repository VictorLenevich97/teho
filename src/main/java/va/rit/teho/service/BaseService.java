package va.rit.teho.service;

import va.rit.teho.entity.Base;

import java.util.List;

public interface BaseService {

    Long add(String shortName, String fullName);

    void addEquipmentToBase(Long baseId, Long equipmentId, int intensity, int amount);

    Base get(Long baseId);

    List<Base> list();
}
