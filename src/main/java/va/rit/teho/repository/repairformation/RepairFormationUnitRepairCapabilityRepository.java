package va.rit.teho.repository.repairformation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapabilityPK;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairFormationUnitRepairCapabilityRepository
        extends CrudRepository<RepairFormationUnitRepairCapability, RepairFormationUnitRepairCapabilityPK> {

    @Query("SELECT c FROM RepairFormationUnitRepairCapability c WHERE " +
            "c.tehoSession.id = :sessionId AND " +
            "c.repairType.id = :repairTypeId AND " +
            "(coalesce(:repairFormationUnitIds, null) is null or c.repairFormationUnit.id in (:repairFormationUnitIds)) AND " +
            "(coalesce(:equipmentIds, null) is null or c.equipment.id in (:equipmentIds)) AND " +
            "(coalesce(:equipmentSubTypeIds, null) is null or c.equipment.equipmentSubType.id in (:equipmentSubTypeIds)) AND " +
            "(coalesce(:equipmentTypeIds, null) is null or c.equipment.equipmentSubType.equipmentType.id in (:equipmentTypeIds)) " +
            "ORDER BY c.repairFormationUnit.id ASC, c.equipment.equipmentSubType.id ASC, c.equipment.equipmentSubType.equipmentType.id ASC, c.equipment.id ASC")
    List<RepairFormationUnitRepairCapability> findByIds(UUID sessionId,
                                                        Long repairTypeId,
                                                        List<Long> repairFormationUnitIds,
                                                        List<Long> equipmentIds,
                                                        List<Long> equipmentSubTypeIds,
                                                        List<Long> equipmentTypeIds);

}
