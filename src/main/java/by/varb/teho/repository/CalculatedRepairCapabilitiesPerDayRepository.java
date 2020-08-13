package by.varb.teho.repository;

import by.varb.teho.entity.CalculatedRepairCapabilitesPerDay;
import by.varb.teho.entity.EquipmentPerRepairStation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculatedRepairCapabilitiesPerDayRepository extends CrudRepository<CalculatedRepairCapabilitesPerDay, EquipmentPerRepairStation> {
}
