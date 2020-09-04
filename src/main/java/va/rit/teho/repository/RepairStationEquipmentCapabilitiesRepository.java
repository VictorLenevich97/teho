package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentPerRepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;

import java.util.List;

@Repository
public interface RepairStationEquipmentCapabilitiesRepository
        extends CrudRepository<RepairStationEquipmentStaff, EquipmentPerRepairStation> {
    List<RepairStationEquipmentStaff> findAllByRepairStationId(Long repairStationId);
}
