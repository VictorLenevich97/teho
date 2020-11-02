package va.rit.teho.repository.repairstation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairstation.RepairStation;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairStationRepository extends CrudRepository<RepairStation, Long> {

    Optional<RepairStation> findByName(String name);

    @Query("SELECT rs from RepairStation rs WHERE (coalesce(:repairStationIds, null) is null or rs.id in (:repairStationIds)) " +
            "ORDER BY rs.id ASC")
    List<RepairStation> findSorted(List<Long> repairStationIds);

}
