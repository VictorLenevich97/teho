package va.rit.teho.repository.repairstation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairstation.RepairStationRepairCapability;
import va.rit.teho.entity.repairstation.RepairStationRepairCapabilityPK;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairStationRepairCapabilityRepository
        extends CrudRepository<RepairStationRepairCapability, RepairStationRepairCapabilityPK> {

    @Query("SELECT c FROM RepairStationRepairCapability c WHERE " +
            "c.tehoSession.id = :sessionId AND " +
            "c.repairType.id = :repairTypeId AND " +
            "(coalesce(:repairStationIds, null) is null or c.repairStation.id in (:repairStationIds)) AND " +
            "(coalesce(:equipmentIds, null) is null or c.equipment.id in (:equipmentIds)) AND " +
            "(coalesce(:equipmentSubTypeIds, null) is null or c.equipment.equipmentSubType.id in (:equipmentSubTypeIds)) AND " +
            "(coalesce(:equipmentTypeIds, null) is null or c.equipment.equipmentSubType.equipmentType.id in (:equipmentTypeIds)) " +
            "ORDER BY c.repairStation.id ASC, c.equipment.equipmentSubType.id ASC, c.equipment.equipmentSubType.equipmentType.id ASC, c.equipment.id ASC")
    List<RepairStationRepairCapability> findByIds(UUID sessionId,
                                                  Long repairTypeId,
                                                  List<Long> repairStationIds,
                                                  List<Long> equipmentIds,
                                                  List<Long> equipmentSubTypeIds,
                                                  List<Long> equipmentTypeIds);

}
