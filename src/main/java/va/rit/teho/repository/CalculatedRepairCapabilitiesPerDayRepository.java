package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.CalculatedRepairCapabilitesPerDay;
import va.rit.teho.entity.EquipmentPerRepairStation;

import java.util.List;

@Repository
public interface CalculatedRepairCapabilitiesPerDayRepository extends CrudRepository<CalculatedRepairCapabilitesPerDay, EquipmentPerRepairStation> {
    List<CalculatedRepairCapabilitesPerDay> findByRepairStationIdIn(List<Long> repairStationIds);
}
