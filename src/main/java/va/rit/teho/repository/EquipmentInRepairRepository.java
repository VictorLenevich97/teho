package va.rit.teho.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.*;

import java.util.*;

@Repository
public interface EquipmentInRepairRepository
        extends CrudRepository<EquipmentInRepair, EquipmentInRepairId>, JpaSpecificationExecutor<EquipmentInRepair> {

    List<EquipmentInRepair> findByTehoSessionId(UUID sessionId);

    @Query("SELECT new va.rit.teho.entity.EquipmentInRepairData(eir.equipment.equipmentSubType, " +
            "eir.base.fullName, " +
            "eir.equipment.id, " +
            "eir.equipment.name, " +
            "ipt.amount, " +
            "eir.workhoursDistributionInterval.id, " +
            "eir.count, " +
            "eir.avgLaborInput) FROM EquipmentInRepair eir " +
            "INNER JOIN EquipmentLaborInputPerType ipt ON eir.equipment.id = ipt.equipment.id " +
            "WHERE ipt.repairType.id = :repairTypeId AND eir.tehoSession.id = :sessionId AND" +
            "(coalesce(:equipmentTypeIds, null) is null or eir.equipment.equipmentSubType.equipmentType.id IN (:equipmentTypeIds)) " +
            "GROUP BY eir.equipment.equipmentSubType, " +
            "eir.base.fullName, " +
            "eir.equipment.id, " +
            "eir.equipment.name, " +
            "ipt.amount, " +
            "eir.workhoursDistributionInterval.id, " +
            "eir.count, " +
            "eir.avgLaborInput")
    List<EquipmentInRepairData> findAllAsData(UUID sessionId, Long repairTypeId, List<Long> equipmentTypeIds);

    default Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentInRepairData.CompositeKey, List<EquipmentInRepairData>>>> findAllGrouped(
            UUID sessionId,
            Long repairTypeId,
            List<Long> equipmentTypeIds) {
        Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentInRepairData.CompositeKey, List<EquipmentInRepairData>>>> result = new HashMap<>();
        for (EquipmentInRepairData temp : findAllAsData(sessionId, repairTypeId, equipmentTypeIds)) {
            result
                    .computeIfAbsent(temp.getCompositeKey().getSubType().getEquipmentType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getCompositeKey().getSubType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getCompositeKey(), k -> new ArrayList<>())
                    .add(temp);
        }
        return result;
    }


    @Query(value = "select equipment.name, sum(amount) as amount, sum(equipment_in_repair.count) as c " +
            "from equipment_in_repair left join equipment_per_base " +
            "on equipment_in_repair.equipment_id = equipment_per_base.equipment_id and equipment_in_repair.base_id = equipment_per_base.base_id " +
            "inner join workhours_distribution_interval on equipment_in_repair.workhours_distribution_interval_id = workhours_distribution_interval.id " +
            "inner join equipment on equipment_in_repair.equipment_id = equipment.id " +
            "group by equipment.name", nativeQuery = true)
    List<List<Object>> findAllGroupedByEquipmentName();

    @Query(value = "select " +
            "(select coalesce(sum(equipment_in_repair.count), 0) from equipment_in_repair " +
            "inner join workhours_distribution_interval on equipment_in_repair.workhours_distribution_interval_id = workhours_distribution_interval.id " +
            "inner join equipment on equipment_in_repair.equipment_id = equipment.id " +
            "where workhours_distribution_interval.restoration_type_id = 1 and equipment.name = ?1" +
            ") as tactical, " +
            "(select coalesce(sum(equipment_in_repair.count), 0) from equipment_in_repair " +
            "inner join workhours_distribution_interval on equipment_in_repair.workhours_distribution_interval_id = workhours_distribution_interval.id " +
            "inner join equipment on equipment_in_repair.equipment_id = equipment.id " +
            "where workhours_distribution_interval.restoration_type_id = 2 and equipment.name = ?1) as operational, " +
            "(select coalesce(sum(equipment_in_repair.count), 0) from equipment_in_repair " +
            "inner join workhours_distribution_interval on equipment_in_repair.workhours_distribution_interval_id = workhours_distribution_interval.id " +
            "inner join equipment on equipment_in_repair.equipment_id = equipment.id " +
            " where workhours_distribution_interval.restoration_type_id = 3 and equipment.name = ?1) as stratigic", nativeQuery = true)
    List<List<Object>> sumEquipmentByRestorationLevelTypes(String equipmentName);
}
