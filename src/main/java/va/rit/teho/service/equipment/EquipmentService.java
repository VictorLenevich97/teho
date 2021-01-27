package va.rit.teho.service.equipment;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.List;
import java.util.Map;

public interface EquipmentService {

    List<Equipment> list();

    Map<Equipment, Map<RepairType, Integer>> listWithLaborInputPerType(List<Long> ids,
                                                                       List<Long> typeIds);

    Equipment get(Long equipmentId);

    Equipment add(String name, Long subTypeId);

    Equipment add(String name, Long subTypeId, Map<Long, Integer> repairTypeIdLaborInputMap);

    Equipment update(Long id, String name, Long subTypeId, Map<Long, Integer> repairTypeIdLaborInputMap);

    void delete(Long id);

    Map<EquipmentType, List<Equipment>> listGroupedByTypes(List<Long> ids,
                                                           List<Long> typeIds);

}
