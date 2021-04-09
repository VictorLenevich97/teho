package va.rit.teho.service.equipment;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;

import java.util.List;
import java.util.Map;

public interface EquipmentService {

    List<Equipment> list();

    Long count(String nameFilter);

    Map<Equipment, Map<RepairType, Integer>> listWithLaborInputPerType(String nameFilter,
                                                                       Integer pageNum,
                                                                       Integer pageSize);

    Equipment get(Long equipmentId);

    Equipment add(String name, Long typeId);

    Equipment add(String name, Long typeId, Map<Long, Integer> repairTypeIdLaborInputMap);

    Equipment update(Long id, String name, Long typeId, Map<Long, Integer> repairTypeIdLaborInputMap);

    void delete(Long id);

}
