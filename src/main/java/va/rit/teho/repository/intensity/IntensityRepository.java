package va.rit.teho.repository.intensity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.intensity.Intensity;
import va.rit.teho.entity.intensity.IntensityPK;

import java.util.List;

@Repository
public interface IntensityRepository extends CrudRepository<Intensity, IntensityPK> {
    @Query("SELECT intensity from Intensity intensity INNER JOIN Operation op ON intensity.operation.id = op.id WHERE op.active = true")
    List<Intensity> listActive();
}
