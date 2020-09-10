package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public interface EquipmentRepository extends CrudRepository<Equipment, Long> {

    default Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> getEquipmentGroupedByType(List<Long> ids,
                                                                                                 List<Long> subTypeIds,
                                                                                                 List<Long> typeIds) {
        Iterable<Equipment> totalEquipmentList;
        if (ids.isEmpty() && subTypeIds.isEmpty() && typeIds.isEmpty()) {
            totalEquipmentList = findAll();
        } else if (ids.isEmpty() && subTypeIds.isEmpty()) {
            totalEquipmentList = findByEquipmentSubTypeEquipmentTypeIdIn(typeIds);
        } else if (ids.isEmpty() && typeIds.isEmpty()) {
            totalEquipmentList = findByEquipmentSubTypeIdIn(subTypeIds);
        } else if (subTypeIds.isEmpty() && typeIds.isEmpty()) {
            totalEquipmentList = findByIdIn(ids);
        } else if (ids.isEmpty()) {
            totalEquipmentList = findByEquipmentSubTypeIdInAndEquipmentSubTypeEquipmentTypeIdIn(subTypeIds, typeIds);
        } else if (subTypeIds.isEmpty()) {
            totalEquipmentList = findByIdInAndEquipmentSubTypeEquipmentTypeIdIn(ids, typeIds);
        } else if (typeIds.isEmpty()) {
            totalEquipmentList = findByIdInAndEquipmentSubTypeIdIn(ids, subTypeIds);
        } else {
            totalEquipmentList = findByIdInAndEquipmentSubTypeIdInAndEquipmentSubTypeEquipmentTypeIdIn(ids,
                                                                                                       subTypeIds,
                                                                                                       typeIds);
        }
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> resultMap = new HashMap<>();
        for (Map.Entry<EquipmentSubType, List<Equipment>> entry :
                StreamSupport
                        .stream(totalEquipmentList.spliterator(), false)
                        .collect(Collectors.groupingBy(Equipment::getEquipmentSubType))
                        .entrySet()) {
            EquipmentSubType subType = entry.getKey();
            List<Equipment> equipmentList = entry.getValue();
            Map<EquipmentSubType, List<Equipment>> subTypeMap =
                    resultMap.computeIfAbsent(subType.getEquipmentType(), k -> new HashMap<>());
            for (Equipment equipment : equipmentList) {
                subTypeMap.computeIfAbsent(equipment.getEquipmentSubType(), k -> new ArrayList<>()).add(equipment);
            }
            resultMap.put(subType.getEquipmentType(), subTypeMap);
        }
        return resultMap;
    }

    List<Equipment> findByIdIn(List<Long> ids);

    List<Equipment> findByEquipmentSubTypeIdIn(List<Long> ids);

    List<Equipment> findByIdInAndEquipmentSubTypeIdIn(List<Long> ids, List<Long> subTypeIds);

    List<Equipment> findByEquipmentSubTypeEquipmentTypeIdIn(List<Long> typeIds);

    List<Equipment> findByEquipmentSubTypeIdInAndEquipmentSubTypeEquipmentTypeIdIn(List<Long> subTypeIds,
                                                                                   List<Long> typeIds);

    List<Equipment> findByIdInAndEquipmentSubTypeEquipmentTypeIdIn(List<Long> ids, List<Long> typeIds);

    List<Equipment> findByIdInAndEquipmentSubTypeIdInAndEquipmentSubTypeEquipmentTypeIdIn(List<Long> ids,
                                                                                          List<Long> subTypeIds,
                                                                                          List<Long> typeIds);

    Optional<Equipment> findByName(String name);
}
