package by.varb.teho.repository;

import by.varb.teho.entity.EquipmentPerRepairStation;
import by.varb.teho.entity.RepairStationEquipmentCapabilities;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairStationEquipmentCapabilitiesRepository extends CrudRepository<RepairStationEquipmentCapabilities, EquipmentPerRepairStation> {
}
