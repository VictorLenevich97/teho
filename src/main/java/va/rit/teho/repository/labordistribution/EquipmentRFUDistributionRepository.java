package va.rit.teho.repository.labordistribution;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import va.rit.teho.entity.equipment.combined.EquipmentPerRestorationTypeAmount;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistribution;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistributionPK;

import java.util.List;
import java.util.UUID;

public interface EquipmentRFUDistributionRepository
        extends CrudRepository<EquipmentRFUDistribution, EquipmentRFUDistributionPK> {

    @Query("SELECT new va.rit.teho.entity.labordistribution.EquipmentRFUDistribution(erd.formation, erd.equipment, erd.repairFormationUnit, MAX(erd.workhoursDistributionInterval), SUM(erd.repairing), SUM(erd.unable)) FROM EquipmentRFUDistribution erd " +
            "WHERE erd.tehoSession.id = :sessionId GROUP BY erd.formation, erd.equipment, erd.repairFormationUnit, erd.workhoursDistributionInterval.restorationType")
    List<EquipmentRFUDistribution> findWithCombinedRestorationTypeData(UUID sessionId);

    List<EquipmentRFUDistribution> findByTehoSessionId(UUID sessionId);

    List<EquipmentRFUDistribution> findByRepairFormationUnitIdAndTehoSessionId(Long repairFormationUnitId,
                                                                               UUID sessionId);

    @Query("SELECT new va.rit.teho.entity.equipment.combined.EquipmentPerRestorationTypeAmount(erd.equipment, erd.workhoursDistributionInterval.restorationType, SUM(erd.repairing)) FROM EquipmentRFUDistribution erd " +
            "WHERE erd.tehoSession.id = :sessionId AND erd.formation.id = :formationId " +
            "GROUP BY erd.tehoSession, erd.equipment, erd.workhoursDistributionInterval.restorationType")
    List<EquipmentPerRestorationTypeAmount> listRepairingAmountPerRestorationType(UUID sessionId, Long formationId);

}
