package va.rit.teho.repository.equipment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentFailurePerRepairTypeAmount;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensityAndLaborInput;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensityPK;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipmentPerFormationFailureIntensityRepository
        extends CrudRepository<EquipmentPerFormationFailureIntensity, EquipmentPerFormationFailureIntensityPK> {

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensityAndLaborInput(epbfi.formation.id, epbfi.equipment.id, epbfi.stage.id, elipt.repairType.id, epbfi.intensityPercentage, epbfi.avgDailyFailure, elipt.amount) FROM " +
            "EquipmentPerFormationFailureIntensity epbfi INNER JOIN EquipmentLaborInputPerType elipt ON elipt.repairType.id = epbfi.repairType.id AND epbfi.equipment.id = elipt.equipment.id " +
            "WHERE (coalesce(:equipmentIds, null) IS NULL OR epbfi.equipment.id IN (:equipmentIds)) AND " +
            "(coalesce(:formationIds, null) IS NULL OR epbfi.formation.id IN (:formationIds)) AND " +
            "epbfi.tehoSession.id = :sessionId AND elipt.repairType.id = :repairTypeId AND epbfi.avgDailyFailure IS NOT NULL")
    List<EquipmentPerFormationFailureIntensityAndLaborInput> findAllWithLaborInput(UUID sessionId,
                                                                                   Long repairTypeId,
                                                                                   List<Long> equipmentIds,
                                                                                   List<Long> formationIds);

    List<EquipmentPerFormationFailureIntensity> findAllByTehoSessionId(UUID sessionId);

    @Query("SELECT epbfi from EquipmentPerFormationFailureIntensity epbfi WHERE epbfi.tehoSession.id = :sessionId AND epbfi.formation.id = :formationId AND " +
            "(coalesce(:equipmentIds, null) IS NULL OR epbfi.equipment.id IN (:equipmentIds))")
    List<EquipmentPerFormationFailureIntensity> findAllByTehoSessionIdAndFormationId(UUID sessionId,
                                                                                     Long formationId,
                                                                                     List<Long> equipmentIds);

    @Query("SELECT epbfi from EquipmentPerFormationFailureIntensity epbfi WHERE " +
            "epbfi.tehoSession.id = :sessionId AND " +
            "epbfi.formation.id = :formationId AND " +
            "epbfi.equipment.id = :equipmentId AND " +
            "epbfi.stage.id = :stageId AND " +
            "epbfi.repairType.id = :repairTypeId")
    Optional<EquipmentPerFormationFailureIntensity> find(UUID sessionId,
                                                         Long formationId,
                                                         Long equipmentId,
                                                         Long stageId,
                                                         Long repairTypeId);

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentFailurePerRepairTypeAmount(epffi.id.equipmentPerFormation, epffi.repairType, SUM(epffi.avgDailyFailure)) FROM EquipmentPerFormationFailureIntensity epffi " +
            "WHERE epffi.tehoSession.id = :sessionId AND epffi.formation.id = :formationId " +
            "GROUP BY epffi.equipment.id, epffi.repairType.id")
    List<EquipmentFailurePerRepairTypeAmount> listFailureDataPerRepairType(UUID sessionId, Long formationId);

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentFailurePerRepairTypeAmount(epffi.id.equipmentPerFormation, epffi.repairType, elipt.amount, SUM(epffi.avgDailyFailure)) FROM EquipmentPerFormationFailureIntensity epffi " +
            "LEFT OUTER JOIN EquipmentLaborInputPerType elipt ON epffi.equipment = elipt.equipment AND epffi.repairType = elipt.repairType " +
            "WHERE epffi.tehoSession.id = :sessionId " +
            "GROUP BY epffi.formation.id, epffi.equipment.id, epffi.repairType.id, elipt.amount")
    List<EquipmentFailurePerRepairTypeAmount> listFailureDataWithLaborInputPerRepairType(UUID sessionId);
}
