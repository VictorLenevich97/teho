package va.rit.teho.repository.formation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.formation.Formation;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormationRepository extends CrudRepository<Formation, Long> {

    Optional<Formation> findByFullName(String fullName);

    List<Formation> findFormationByParentFormationIsNull();
}
