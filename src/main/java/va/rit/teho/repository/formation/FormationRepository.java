package va.rit.teho.repository.formation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.formation.Formation;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormationRepository extends CrudRepository<Formation, Long> {

    @Query("SELECT COALESCE(max(formation.id), 0) FROM Formation formation")
    Long getMaxId();

    Optional<Formation> findByFullName(String fullName);

    List<Formation> findFormationByParentFormationIsNull();
}
