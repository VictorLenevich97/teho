package va.rit.teho.repository.labordistribution;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.labordistribution.RestorationType;

import java.util.List;

@Repository
public interface RestorationTypeRepository extends CrudRepository<RestorationType, Long> {

    @Query("SELECT COALESCE(max(rt.id), 0) FROM RestorationType rt")
    Long getMaxId();

    List<RestorationType> findByNameIgnoreCase(String name);

}
