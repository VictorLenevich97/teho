package va.rit.teho.repository.labordistribution;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.LaborDistribution;
import va.rit.teho.entity.labordistribution.LaborDistributionData;
import va.rit.teho.entity.labordistribution.LaborDistributionPK;

import java.util.*;

@Repository
public interface LaborDistributionRepository
        extends CrudRepository<LaborDistribution, LaborDistributionPK>, JpaSpecificationExecutor<LaborDistribution> {

    List<LaborDistribution> findByTehoSessionId(UUID sessionId);

    @Query("SELECT new va.rit.teho.entity.labordistribution.LaborDistributionData(ld.equipment.equipmentSubType, " +
            "ld.base.fullName, " +
            "ld.equipment.id, " +
            "ld.equipment.name, " +
            "ipt.amount, " +
            "ld.workhoursDistributionInterval.id, " +
            "ld.count, " +
            "ld.avgLaborInput) FROM LaborDistribution ld " +
            "INNER JOIN EquipmentLaborInputPerType ipt ON ld.equipment.id = ipt.equipment.id " +
            "WHERE ipt.repairType.id = :repairTypeId AND ld.tehoSession.id = :sessionId AND" +
            "(coalesce(:equipmentTypeIds, null) is null or ld.equipment.equipmentSubType.equipmentType.id IN (:equipmentTypeIds)) " +
            "AND ld.stage.id = :stageId " +
            "GROUP BY ld.equipment.equipmentSubType, " +
            "ld.base.fullName, " +
            "ld.equipment.id, " +
            "ld.equipment.name, " +
            "ipt.amount, " +
            "ld.workhoursDistributionInterval.id, " +
            "ld.count, " +
            "ld.avgLaborInput")
    List<LaborDistributionData> findAllAsData(UUID sessionId,
                                              Long repairTypeId,
                                              Long stageId,
                                              List<Long> equipmentTypeIds);

    default Map<EquipmentType, Map<EquipmentSubType, Map<LaborDistributionData.CompositeKey, List<LaborDistributionData>>>> findAllGrouped(
            UUID sessionId,
            Long repairTypeId,
            Long stageId,
            List<Long> equipmentTypeIds) {
        Map<EquipmentType, Map<EquipmentSubType, Map<LaborDistributionData.CompositeKey, List<LaborDistributionData>>>> result = new HashMap<>();
        for (LaborDistributionData temp : findAllAsData(sessionId, repairTypeId, stageId, equipmentTypeIds)) {
            result
                    .computeIfAbsent(temp.getCompositeKey().getSubType().getEquipmentType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getCompositeKey().getSubType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getCompositeKey(), k -> new ArrayList<>())
                    .add(temp);
        }
        return result;
    }

    @Query(value = "select equipment.name, sum(amount) as amount, sum(labor_distribution.count) as c " +
            "from labor_distribution left join equipment_per_base " +
            "on labor_distribution.equipment_id = equipment_per_base.equipment_id and labor_distribution.base_id = equipment_per_base.base_id " +
            "inner join workhours_distribution_interval on labor_distribution.workhours_distribution_interval_id = workhours_distribution_interval.id " +
            "inner join equipment on labor_distribution.equipment_id = equipment.id " +
            "group by equipment.name", nativeQuery = true)
    List<List<Object>> findAllGroupedByEquipmentName();

    @Query(value = "select " +
            "(select coalesce(sum(labor_distribution.count), 0) from labor_distribution " +
            "inner join workhours_distribution_interval on labor_distribution.workhours_distribution_interval_id = workhours_distribution_interval.id " +
            "inner join equipment on labor_distribution.equipment_id = equipment.id " +
            "where workhours_distribution_interval.restoration_type_id = 1 and equipment.name = ?1" +
            ") as tactical, " +
            "(select coalesce(sum(labor_distribution.count), 0) from labor_distribution " +
            "inner join workhours_distribution_interval on labor_distribution.workhours_distribution_interval_id = workhours_distribution_interval.id " +
            "inner join equipment on labor_distribution.equipment_id = equipment.id " +
            "where workhours_distribution_interval.restoration_type_id = 2 and equipment.name = ?1) as operational, " +
            "(select coalesce(sum(labor_distribution.count), 0) from labor_distribution " +
            "inner join workhours_distribution_interval on labor_distribution.workhours_distribution_interval_id = workhours_distribution_interval.id " +
            "inner join equipment on labor_distribution.equipment_id = equipment.id " +
            " where workhours_distribution_interval.restoration_type_id = 3 and equipment.name = ?1) as stratigic", nativeQuery = true)
    List<List<Object>> sumEquipmentByRestorationLevelTypes(String equipmentName);

}
