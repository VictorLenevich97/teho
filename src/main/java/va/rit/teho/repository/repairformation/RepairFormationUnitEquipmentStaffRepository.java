package va.rit.teho.repository.repairformation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairFormationUnitPK;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairFormationUnitEquipmentStaffRepository
        extends CrudRepository<RepairFormationUnitEquipmentStaff, RepairFormationUnitPK> {
    List<RepairFormationUnitEquipmentStaff> findAllByTehoSessionId(UUID sessionId);

    List<RepairFormationUnitEquipmentStaff> findAllByRepairFormationUnitId(Long repairFormationUnitId);

    @Query("SELECT rses from RepairFormationUnitEquipmentStaff rses WHERE " +
            "rses.repairFormationUnit.repairFormation.formation.tehoSession.id = :sessionId AND " +
            "(coalesce(:repairFormationUnitIds, null) is null or rses.repairFormationUnit.id in (:repairFormationUnitIds)) AND " +
            "(coalesce(:equipmentTypeIds, null) is null or rses.equipmentType.id in (:equipmentTypeIds) or " +
            "(rses.equipmentType.parentType IS NOT NULL AND rses.equipmentType.parentType.id IN (:equipmentTypeIds)))")
    List<RepairFormationUnitEquipmentStaff> findFiltered(UUID sessionId,
                                                         List<Long> repairFormationUnitIds,
                                                         List<Long> equipmentTypeIds);

}
