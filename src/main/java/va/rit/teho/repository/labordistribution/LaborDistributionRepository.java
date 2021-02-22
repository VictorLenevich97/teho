package va.rit.teho.repository.labordistribution;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.LaborDistribution;
import va.rit.teho.entity.labordistribution.LaborDistributionPK;
import va.rit.teho.entity.labordistribution.combined.LaborDistributionAggregatedData;
import va.rit.teho.entity.labordistribution.combined.LaborDistributionData;

import java.util.*;

@Repository
public interface LaborDistributionRepository
        extends CrudRepository<LaborDistribution, LaborDistributionPK>, JpaSpecificationExecutor<LaborDistribution> {

    List<LaborDistribution> findByTehoSessionId(UUID sessionId);

    @Query("SELECT ld FROM LaborDistribution ld WHERE ld.tehoSession.id = :sessionId AND " +
            "(coalesce(:formationIds, null) is null or ld.formation.id IN (:formationIds)) AND " +
            "(coalesce(:equipmentIds, null) is null or ld.equipment.id IN (:equipmentIds)) ")
    List<LaborDistribution> findByTehoSessionIdAndFilters(UUID sessionId,
                                                          List<Long> formationIds,
                                                          List<Long> equipmentIds);

    @Query("SELECT new va.rit.teho.entity.labordistribution.combined.LaborDistributionData(ld.laborDistributionId.equipmentPerFormation, " +
            "ipt.amount, " +
            "ld.workhoursDistributionInterval.id, " +
            "ld.count, " +
            "ld.avgLaborInput, " +
            "epbfi.avgDailyFailure) FROM LaborDistribution ld " +
            "INNER JOIN EquipmentLaborInputPerType ipt ON ld.equipment.id = ipt.equipment.id " +
            "INNER JOIN EquipmentPerFormationFailureIntensity epbfi on (ld.equipment.id = epbfi.equipment.id and ld.formation.id = epbfi.formation.id and ld.tehoSession.id = epbfi.tehoSession.id and ld.stage.id = epbfi.stage.id and ipt.repairType.id = epbfi.repairType.id) " +
            "WHERE ipt.repairType.id = :repairTypeId AND ld.tehoSession.id = :sessionId AND" +
            "(coalesce(:equipmentTypeIds, null) is null or " +
            "ld.equipment.equipmentType.id IN (:equipmentTypeIds) or " +
            "(ld.equipment.equipmentType.parentType IS NOT NULL AND ld.equipment.equipmentType.parentType.id IN (:equipmentTypeIds))) " +
            "AND ld.stage.id = :stageId AND ld.repairType.id = :repairTypeId")
    List<LaborDistributionData> findAllAsData(UUID sessionId,
                                              Long repairTypeId,
                                              Long stageId,
                                              List<Long> equipmentTypeIds);

    default Map<EquipmentType, Map<EquipmentPerFormation, List<LaborDistributionData>>> findAllGrouped(
            UUID sessionId,
            Long repairTypeId,
            Long stageId,
            List<Long> equipmentTypeIds) {
        Map<EquipmentType, Map<EquipmentPerFormation, List<LaborDistributionData>>> result = new HashMap<>();
        for (LaborDistributionData temp : findAllAsData(sessionId, repairTypeId, stageId, equipmentTypeIds)) {
            result
                    .computeIfAbsent(temp
                                             .getEquipmentPerFormation()
                                             .getEquipment()
                                             .getEquipmentType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getEquipmentPerFormation(), k -> new ArrayList<>())
                    .add(temp);
        }
        return result;
    }

    @Query(value = "SELECT new va.rit.teho.entity.labordistribution.combined.LaborDistributionAggregatedData(ld.equipment, ld.formation, ld.workhoursDistributionInterval, sum(ld.count), avg(ld.avgLaborInput)) " +
            "FROM LaborDistribution ld " +
            "WHERE ld.formation.id = :formationId AND ld.tehoSession.id = :sessionId AND (coalesce(:equipmentIds, null) is null or ld.equipment.id IN (:equipmentIds)) " +
            "GROUP BY ld.equipment, ld.formation, ld.workhoursDistributionInterval")
    List<LaborDistributionAggregatedData> selectLaborDistributionAggregatedData(UUID sessionId,
                                                                                Long formationId,
                                                                                List<Long> equipmentIds);

}
