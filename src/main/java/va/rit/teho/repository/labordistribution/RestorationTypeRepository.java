package va.rit.teho.repository.labordistribution;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.labordistribution.RestorationType;

@Repository
public interface RestorationTypeRepository extends CrudRepository<RestorationType, Long> {
}
