package va.rit.teho.repository.intensity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.intensity.Intensity;
import va.rit.teho.entity.intensity.IntensityPK;

import java.util.List;

@Repository
public interface IntensityRepository extends CrudRepository<Intensity, IntensityPK> {

    List<Intensity> findByOperationActiveIsTrue();

    List<Intensity> findByOperationId(Long id);
}
