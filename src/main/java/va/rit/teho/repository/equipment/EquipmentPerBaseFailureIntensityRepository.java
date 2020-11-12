package va.rit.teho.repository.equipment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensity;
import va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensityAndAmount;
import va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensityAndLaborInput;
import va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensityPK;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentPerBaseFailureIntensityRepository
        extends CrudRepository<EquipmentPerBaseFailureIntensity, EquipmentPerBaseFailureIntensityPK> {

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensityAndAmount(epbfi.base.id, epbfi.equipment.id, epbfi.stage.id, epbfi.repairType.id, epbfi.intensityPercentage, epb.amount) FROM " +
            "EquipmentPerBaseFailureIntensity epbfi INNER JOIN EquipmentPerBase epb ON epbfi.base.id = epb.base.id AND epbfi.equipment.id = epb.equipment.id WHERE epbfi.tehoSession.id = :sessionId")
    List<EquipmentPerBaseFailureIntensityAndAmount> findAllWithIntensityAndAmount(UUID sessionId);

    @Query("SELECT new va.rit.teho.entity.equipment.EquipmentPerBaseFailureIntensityAndLaborInput(epbfi.base.id, epbfi.equipment.id, epbfi.stage.id, elipt.repairType.id, epbfi.intensityPercentage, epbfi.avgDailyFailure, elipt.amount) FROM " +
            "EquipmentPerBaseFailureIntensity epbfi INNER JOIN EquipmentLaborInputPerType elipt ON elipt.repairType.id = epbfi.repairType.id AND epbfi.equipment.id = elipt.equipment.id WHERE epbfi.tehoSession.id = :sessionId AND elipt.repairType.id = :repairTypeId AND epbfi.avgDailyFailure IS NOT NULL")
    List<EquipmentPerBaseFailureIntensityAndLaborInput> findAllWithLaborInput(UUID sessionId, Long repairTypeId);

    List<EquipmentPerBaseFailureIntensity> findAllByTehoSessionId(UUID sessionId);

    List<EquipmentPerBaseFailureIntensity> findAllByTehoSessionIdAndBaseId(UUID sessionId, Long baseId);

}
