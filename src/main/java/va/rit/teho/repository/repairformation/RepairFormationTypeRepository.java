package va.rit.teho.repository.repairformation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairformation.RepairFormationType;

import java.util.Optional;

@Repository
public interface RepairFormationTypeRepository extends CrudRepository<RepairFormationType, Long> {

    @Query("SELECT COALESCE(max(rft.id), 0) FROM RepairFormationType rft")
    Long getMaxId();

    Optional<RepairFormationType> findByName(String name);
}
