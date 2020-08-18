package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.RestorationType;

@Repository
public interface RestorationTypeRepository extends CrudRepository<RestorationType, Long> {
}
