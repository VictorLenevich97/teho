package va.rit.teho.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface EquipmentRepository extends CrudRepository<Equipment, Long> {

    default Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> getEquipmentGroupedByType(List<Long> ids,
                                                                                                 List<Long> subTypeIds,
                                                                                                 List<Long> typeIds) {
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> resultMap = new HashMap<>();
        for (Map.Entry<EquipmentSubType, List<Equipment>> entry :
                findFiltered(ids, subTypeIds, typeIds)
                        .stream()
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

    @Query("SELECT e from Equipment e WHERE (coalesce(:ids, null) is null or e.id in (:ids)) AND " +
            "(coalesce(:subTypeIds, null) is null or e.equipmentSubType.id in (:subTypeIds)) AND " +
            "(coalesce(:typeIds, null) is null or e.equipmentSubType.equipmentType.id in (:typeIds))")
    List<Equipment> findFiltered(List<Long> ids, List<Long> subTypeIds, List<Long> typeIds);

    Optional<Equipment> findByName(String name);
}
