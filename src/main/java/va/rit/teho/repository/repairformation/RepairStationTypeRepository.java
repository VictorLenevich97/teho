package va.rit.teho.repository.repairformation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairformation.RepairStationType;

import java.util.Optional;

@Repository
public interface RepairStationTypeRepository extends CrudRepository<RepairStationType, Long> {

    @Query("SELECT COALESCE(max(type.id), 0) FROM RepairStationType type")
    Long getMaxId();

    Optional<RepairStationType> findByNameIgnoreCase(String name);

}
