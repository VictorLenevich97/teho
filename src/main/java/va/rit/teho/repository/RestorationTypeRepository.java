package va.rit.teho.repository;

import va.rit.teho.entity.RestorationType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestorationTypeRepository extends CrudRepository<RestorationType, Long> {
}
