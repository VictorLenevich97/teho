package va.rit.teho.repository.repairformation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairformation.RepairFormation;

import java.util.List;

@Repository
public interface RepairFormationRepository extends CrudRepository<RepairFormation, Long> {

    @Query("SELECT COALESCE(max(rf.id), 0) FROM RepairFormation rf")
    Long getMaxId();

    List<RepairFormation> findAllByFormationId(Long formationId);

}
