package by.varb.teho.repository;

import by.varb.teho.entity.RepairStation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairStationRepository extends CrudRepository<RepairStation, Long> {
}
