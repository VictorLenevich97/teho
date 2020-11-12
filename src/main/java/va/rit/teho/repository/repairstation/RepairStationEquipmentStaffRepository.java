package va.rit.teho.repository.repairstation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairstation.RepairStationEquipmentStaff;
import va.rit.teho.entity.repairstation.RepairStationEquipmentStaffPK;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairStationEquipmentStaffRepository
        extends CrudRepository<RepairStationEquipmentStaff, RepairStationEquipmentStaffPK> {
    List<RepairStationEquipmentStaff> findAllByTehoSessionId(UUID sessionId);

    List<RepairStationEquipmentStaff> findAllByRepairStationIdAndTehoSessionId(Long repairStationId, UUID sessionId);

    @Query("SELECT rses from RepairStationEquipmentStaff rses WHERE " +
            "rses.tehoSession.id = :sessionId AND " +
            "(coalesce(:repairStationIds, null) is null or rses.repairStation.id in (:repairStationIds)) AND " +
            "(coalesce(:equipmentSubTypeIds, null) is null or rses.equipmentSubType.id in (:equipmentSubTypeIds)) AND " +
            "(coalesce(:equipmentTypeIds, null) is null or rses.equipmentSubType.equipmentType.id in (:equipmentTypeIds))")
    List<RepairStationEquipmentStaff> findFiltered(UUID sessionId,
                                                   List<Long> repairStationIds,
                                                   List<Long> equipmentTypeIds,
                                                   List<Long> equipmentSubTypeIds);
}
