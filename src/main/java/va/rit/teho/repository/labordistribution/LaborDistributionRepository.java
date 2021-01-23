package va.rit.teho.repository.labordistribution;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.LaborDistribution;
import va.rit.teho.entity.labordistribution.LaborDistributionAggregatedData;
import va.rit.teho.entity.labordistribution.LaborDistributionData;
import va.rit.teho.entity.labordistribution.LaborDistributionPK;

import java.util.*;

@Repository
public interface LaborDistributionRepository
        extends CrudRepository<LaborDistribution, LaborDistributionPK>, JpaSpecificationExecutor<LaborDistribution> {

    List<LaborDistribution> findByTehoSessionId(UUID sessionId);

    @Query("SELECT new va.rit.teho.entity.labordistribution.LaborDistributionData(ld.laborDistributionId.equipmentPerFormation, " +
            "ipt.amount, " +
            "ld.workhoursDistributionInterval.id, " +
            "ld.count, " +
            "ld.avgLaborInput, " +
            "epbfi.avgDailyFailure) FROM LaborDistribution ld " +
            "INNER JOIN EquipmentLaborInputPerType ipt ON ld.equipment.id = ipt.equipment.id " +
            "INNER JOIN EquipmentPerFormationFailureIntensity epbfi on (ld.equipment.id = epbfi.equipment.id and ld.formation.id = epbfi.formation.id and ld.tehoSession.id = epbfi.tehoSession.id and ld.stage.id = epbfi.stage.id and ipt.repairType.id = epbfi.repairType.id) " +
            "WHERE ipt.repairType.id = :repairTypeId AND ld.tehoSession.id = :sessionId AND" +
            "(coalesce(:equipmentTypeIds, null) is null or ld.equipment.equipmentSubType.equipmentType.id IN (:equipmentTypeIds)) " +
            "AND ld.stage.id = :stageId AND ld.repairType.id = :repairTypeId")
    List<LaborDistributionData> findAllAsData(UUID sessionId,
                                              Long repairTypeId,
                                              Long stageId,
                                              List<Long> equipmentTypeIds);

    default Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentPerFormation, List<LaborDistributionData>>>> findAllGrouped(
            UUID sessionId,
            Long repairTypeId,
            Long stageId,
            List<Long> equipmentTypeIds) {
        Map<EquipmentType, Map<EquipmentSubType, Map<EquipmentPerFormation, List<LaborDistributionData>>>> result = new HashMap<>();
        for (LaborDistributionData temp : findAllAsData(sessionId, repairTypeId, stageId, equipmentTypeIds)) {
            result
                    .computeIfAbsent(temp
                                             .getEquipmentPerFormation()
                                             .getEquipment()
                                             .getEquipmentSubType()
                                             .getEquipmentType(), k -> new HashMap<>())
                    .computeIfAbsent(temp.getEquipmentPerFormation().getEquipment().getEquipmentSubType(),
                                     k -> new HashMap<>())
                    .computeIfAbsent(temp.getEquipmentPerFormation(), k -> new ArrayList<>())
                    .add(temp);
        }
        return result;

    }

    @Query(value = "SELECT new va.rit.teho.entity.labordistribution.LaborDistributionAggregatedData(ld.equipment, ld.formation, ld.workhoursDistributionInterval, sum(ld.count), avg(ld.avgLaborInput)) FROM LaborDistribution ld WHERE ld.formation.id = :formationId AND ld.tehoSession.id = :sessionId GROUP BY ld.equipment, ld.formation, ld.workhoursDistributionInterval")
    List<LaborDistributionAggregatedData> selectLaborDistributionAggregatedData(Long formationId, UUID sessionId);

}
