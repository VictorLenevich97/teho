package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.CalculatedRepairCapabilitesPerDay;
import va.rit.teho.entity.EquipmentPerRepairStation;

@Repository
public interface CalculatedRepairCapabilitiesPerDayRepository extends CrudRepository<CalculatedRepairCapabilitesPerDay, EquipmentPerRepairStation> {
}
