package va.rit.teho.repository.equipment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.*;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentPerFormationFailureIntensityRepository
        extends CrudRepository<EquipmentPerFormationFailureIntensity, EquipmentPerFormationFailureIntensityPK> {

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensityAndAmount(epbfi.formation.id, epbfi.equipment.id, epbfi.stage.id, epbfi.repairType.id, epbfi.intensityPercentage, epb.amount) FROM " +
            "EquipmentPerFormationFailureIntensity epbfi INNER JOIN EquipmentPerFormation epb ON epbfi.formation.id = epb.formation.id AND epbfi.equipment.id = epb.equipment.id WHERE epbfi.tehoSession.id = :sessionId AND epbfi.formation.id = :formationId")
    List<EquipmentPerFormationFailureIntensityAndAmount> findAllWithIntensityAndAmount(UUID sessionId, Long formationId);

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensityAndLaborInput(epbfi.formation.id, epbfi.equipment.id, epbfi.stage.id, elipt.repairType.id, epbfi.intensityPercentage, epbfi.avgDailyFailure, elipt.amount) FROM " +
            "EquipmentPerFormationFailureIntensity epbfi INNER JOIN EquipmentLaborInputPerType elipt ON elipt.repairType.id = epbfi.repairType.id AND epbfi.equipment.id = elipt.equipment.id WHERE epbfi.tehoSession.id = :sessionId AND elipt.repairType.id = :repairTypeId AND epbfi.avgDailyFailure IS NOT NULL")
    List<EquipmentPerFormationFailureIntensityAndLaborInput> findAllWithLaborInput(UUID sessionId, Long repairTypeId);

    List<EquipmentPerFormationFailureIntensity> findAllByTehoSessionId(UUID sessionId);

    @Query("SELECT epbfi from EquipmentPerFormationFailureIntensity epbfi WHERE epbfi.tehoSession.id = :sessionId AND epbfi.formation.id = :formationId AND " +
            "(coalesce(:equipmentIds, null) IS NULL OR epbfi.equipment.id IN (:equipmentIds))")
    List<EquipmentPerFormationFailureIntensity> findAllByTehoSessionIdAndFormationId(UUID sessionId,
                                                                                     Long formationId,
                                                                                     List<Long> equipmentIds);

    @Nullable
    @Query("SELECT epbfi from EquipmentPerFormationFailureIntensity epbfi WHERE " +
            "epbfi.tehoSession.id = :sessionId AND " +
            "epbfi.formation.id = :formationId AND " +
            "epbfi.equipment.id = :equipmentId AND " +
            "epbfi.stage.id = :stageId AND " +
            "epbfi.repairType.id = :repairTypeId")
    EquipmentPerFormationFailureIntensity get(UUID sessionId,
                                              Long formationId,
                                              Long equipmentId,
                                              Long stageId,
                                              Long repairTypeId);

    void deleteByFormationIdAndEquipmentId(Long formationId, Long equipmentId);

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentFailurePerRepairTypeAmount(epffi.equipment, epffi.repairType, SUM(epffi.avgDailyFailure)) FROM EquipmentPerFormationFailureIntensity epffi " +
            "WHERE epffi.tehoSession.id = :sessionId AND epffi.formation.id = :formationId " +
            "GROUP BY epffi.tehoSession.id, epffi.equipment.id, epffi.repairType.id")
    List<EquipmentFailurePerRepairTypeAmount> listFailureDataPerRepairType(UUID sessionId, Long formationId);
}
