package va.rit.teho.service;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import java.util.List;
import java.util.Map;

public interface EquipmentService {

    List<Equipment> list();

    Equipment getEquipment(Long equipmentId);

    Long add(String name, Long subTypeId);

    void update(Long id, String name, Long subTypeId);

    Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> listGroupedByTypes(List<Long> ids,
                                                                                  List<Long> subTypeIds,
                                                                                  List<Long> typeIds);


}
