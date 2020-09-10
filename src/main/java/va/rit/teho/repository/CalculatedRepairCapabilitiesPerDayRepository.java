package va.rit.teho.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.CalculatedRepairCapabilitesPerDay;
import va.rit.teho.entity.EquipmentPerRepairStation;

import java.util.List;

@Repository
public interface CalculatedRepairCapabilitiesPerDayRepository
        extends CrudRepository<CalculatedRepairCapabilitesPerDay, EquipmentPerRepairStation> {

    @Query("SELECT c FROM CalculatedRepairCapabilitesPerDay c WHERE " +
            "(coalesce(:repairStationIds, null) is null or c.repairStation.id in (:repairStationIds)) AND " +
            "(coalesce(:equipmentIds, null) is null or c.equipment.id in (:equipmentIds)) AND " +
            "(coalesce(:equipmentSubTypeIds, null) is null or c.equipment.equipmentSubType.id in (:equipmentSubTypeIds)) AND " +
            "(coalesce(:equipmentTypeIds, null) is null or c.equipment.equipmentSubType.equipmentType.id in (:equipmentTypeIds))")
    List<CalculatedRepairCapabilitesPerDay> findFiltered(
            List<Long> repairStationIds,
            List<Long> equipmentIds,
            List<Long> equipmentSubTypeIds,
            List<Long> equipmentTypeIds);

    List<CalculatedRepairCapabilitesPerDay> findByRepairStationIdIn(List<Long> repairStationIds);

    List<CalculatedRepairCapabilitesPerDay> findAllByEquipmentId(Long equipmentId);
}
