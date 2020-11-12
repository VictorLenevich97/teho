package va.rit.teho.repository.common;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.common.Stage;

@Repository
public interface StageRepository extends CrudRepository<Stage, Long> {
}
