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

    default Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> getEquipmentGroupedByType() {
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> resultMap = new HashMap<>();
        for (Map.Entry<EquipmentSubType, List<Equipment>> entry :
                StreamSupport
                        .stream(findAll().spliterator(), false)
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

    Optional<Equipment> findByName(String name);
}
