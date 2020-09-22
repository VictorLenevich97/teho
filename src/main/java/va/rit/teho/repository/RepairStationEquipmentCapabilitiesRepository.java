package va.rit.teho.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentSubTypePerRepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;

import java.util.List;

@Repository
public interface RepairStationEquipmentCapabilitiesRepository
        extends CrudRepository<RepairStationEquipmentStaff, EquipmentSubTypePerRepairStation> {
    List<RepairStationEquipmentStaff> findAllByRepairStationId(Long repairStationId);

    @Query("SELECT rses from RepairStationEquipmentStaff rses WHERE (coalesce(:repairStationIds, null) is null or rses.repairStation.id in (:repairStationIds)) AND " +
            "(coalesce(:equipmentSubTypeIds, null) is null or rses.equipmentSubType.id in (:equipmentSubTypeIds)) AND " +
            "(coalesce(:equipmentTypeIds, null) is null or rses.equipmentSubType.equipmentType.id in (:equipmentTypeIds))")
    List<RepairStationEquipmentStaff> findFiltered(List<Long> repairStationIds, List<Long> equipmentTypeIds, List<Long> equipmentSubTypeIds);
}
