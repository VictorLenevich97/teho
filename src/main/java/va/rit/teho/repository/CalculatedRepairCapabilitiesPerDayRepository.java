package va.rit.teho.repository;

import va.rit.teho.entity.CalculatedRepairCapabilitesPerDay;
import va.rit.teho.entity.EquipmentPerRepairStation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculatedRepairCapabilitiesPerDayRepository extends CrudRepository<CalculatedRepairCapabilitesPerDay, EquipmentPerRepairStation> {
}
