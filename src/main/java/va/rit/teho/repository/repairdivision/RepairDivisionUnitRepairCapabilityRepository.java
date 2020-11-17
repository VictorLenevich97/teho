package va.rit.teho.repository.repairdivision;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitRepairCapability;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitRepairCapabilityPK;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairDivisionUnitRepairCapabilityRepository
        extends CrudRepository<RepairDivisionUnitRepairCapability, RepairDivisionUnitRepairCapabilityPK> {

    @Query("SELECT c FROM RepairDivisionUnitRepairCapability c WHERE " +
            "c.tehoSession.id = :sessionId AND " +
            "c.repairType.id = :repairTypeId AND " +
            "(coalesce(:repairDivisionUnitIds, null) is null or c.repairDivisionUnit.id in (:repairDivisionUnitIds)) AND " +
            "(coalesce(:equipmentIds, null) is null or c.equipment.id in (:equipmentIds)) AND " +
            "(coalesce(:equipmentSubTypeIds, null) is null or c.equipment.equipmentSubType.id in (:equipmentSubTypeIds)) AND " +
            "(coalesce(:equipmentTypeIds, null) is null or c.equipment.equipmentSubType.equipmentType.id in (:equipmentTypeIds)) " +
            "ORDER BY c.repairDivisionUnit.id ASC, c.equipment.equipmentSubType.id ASC, c.equipment.equipmentSubType.equipmentType.id ASC, c.equipment.id ASC")
    List<RepairDivisionUnitRepairCapability> findByIds(UUID sessionId,
                                                       Long repairTypeId,
                                                       List<Long> repairDivisionUnitIds,
                                                       List<Long> equipmentIds,
                                                       List<Long> equipmentSubTypeIds,
                                                       List<Long> equipmentTypeIds);

}
