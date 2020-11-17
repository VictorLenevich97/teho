package va.rit.teho.repository.repairdivision;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.repairdivision.RepairDivision;

@Repository
public interface RepairDivisionRepository extends CrudRepository<RepairDivision, Long> {

}
