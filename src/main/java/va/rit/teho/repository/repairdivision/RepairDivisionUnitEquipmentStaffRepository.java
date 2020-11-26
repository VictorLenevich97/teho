package va.rit.teho.repository.repairdivision;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitEquipmentStaff;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitPK;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairDivisionUnitEquipmentStaffRepository
        extends CrudRepository<RepairDivisionUnitEquipmentStaff, RepairDivisionUnitPK> {
    List<RepairDivisionUnitEquipmentStaff> findAllByTehoSessionId(UUID sessionId);

    List<RepairDivisionUnitEquipmentStaff> findAllByRepairDivisionUnitIdAndTehoSessionId(Long repairDivisionUnitId, UUID sessionId);

    @Query("SELECT rses from RepairDivisionUnitEquipmentStaff rses WHERE " +
            "rses.tehoSession.id = :sessionId AND " +
            "(coalesce(:repairDivisionUnitIds, null) is null or rses.repairDivisionUnit.id in (:repairDivisionUnitIds)) AND " +
            "(coalesce(:equipmentSubTypeIds, null) is null or rses.equipmentSubType.id in (:equipmentSubTypeIds)) AND " +
            "(coalesce(:equipmentTypeIds, null) is null or rses.equipmentSubType.equipmentType.id in (:equipmentTypeIds))")
    List<RepairDivisionUnitEquipmentStaff> findFiltered(UUID sessionId,
                                                        List<Long> repairDivisionUnitIds,
                                                        List<Long> equipmentTypeIds,
                                                        List<Long> equipmentSubTypeIds);
}
