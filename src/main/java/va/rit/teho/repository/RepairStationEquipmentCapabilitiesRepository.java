package va.rit.teho.repository;

import va.rit.teho.entity.EquipmentPerRepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairStationEquipmentCapabilitiesRepository extends CrudRepository<RepairStationEquipmentStaff, EquipmentPerRepairStation> {
}
